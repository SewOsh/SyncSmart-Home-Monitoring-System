package com.example.syncsmart.data

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val TAG = "UsageRepository"

private const val HourMillis = 60L * 60L * 1000L

/** How much ON time a device accumulated during one specific clock hour. */
data class UsageSlice(val deviceId: String, val hourStart: Long, val ms: Long)

/**
 * Tracks how long each device spends switched ON, using a compact daily rollup
 * rather than one document per toggle.
 *
 * Two collections:
 *
 *  - "usage_state" — exactly 14 documents, one per device, never grows.
 *    `{on: Boolean, since: Long}` = the currently open session. This is what
 *    lets a toggle know how long the device had been on before it changed.
 *
 *  - "usage_daily" — one document per device per DAY, id "{deviceId}_{yyyy-MM-dd}".
 *    `{deviceId, date, dayStart, totalMs, hours: {"0".."23": Long}}`.
 *    Toggling updates this document in place via atomic increments; it does not
 *    create new documents. The per-hour map keeps enough resolution for the
 *    Reports screen's 4-hour Daily buckets while still being a single document.
 *
 * So 14 devices toggled 20 times each in a day writes 14 daily documents, not
 * 280 event documents — and Reports reads ~14 documents per day of range
 * instead of hundreds.
 *
 * There is no power meter in this project — Firestore only stores on/off — so
 * "usage" here means measured ON time. Any kWh shown in the UI is an estimate
 * derived from [estimatedWatts], not a measurement.
 */
object UsageRepository {

    internal const val DailyCollection = "usage_daily"
    internal const val StateCollection = "usage_state"

    private val db get() = FirebaseFirestore.getInstance()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val _lastError = MutableStateFlow<String?>(null)

    /** The most recent usage read/write failure, surfaced on the Reports screen so
     * a failure isn't mistaken for "no usage yet". */
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    // ---------------------------------------------------------------- writing

    /**
     * Called on every device state change. If the device had been ON, the elapsed
     * time since it was switched on is split across the clock hours it spans and
     * added to the relevant daily document(s); then the open session is reset to
     * the new state.
     *
     * Best-effort: a failure here is logged but must not fail the toggle itself.
     */
    suspend fun recordStateChange(deviceId: String, nowOn: Boolean, at: Long = System.currentTimeMillis()) {
        runCatching {
            val stateDoc = db.collection(StateCollection).document(deviceId)
            val previous = stateDoc.get().await()
            val wasOn = previous.getBoolean("on") ?: false
            val since = previous.getLong("since")

            // Close the open session, if any, into the daily buckets.
            if (wasOn && since != null && at > since) {
                accumulate(deviceId, since, at)
            }

            stateDoc.set(mapOf("on" to nowOn, "since" to at)).await()
        }.onFailure { Log.e(TAG, "recordStateChange($deviceId) failed: ${it.message}") }
    }

    /** Splits [from, to) across day + hour boundaries and increments each affected
     * daily document. A span within one hour touches one document and one field;
     * a device left on overnight touches two documents. */
    private suspend fun accumulate(deviceId: String, from: Long, to: Long) {
        // Group the span's per-hour milliseconds by day document id, so a span
        // crossing midnight results in one write per day rather than per hour.
        val perDay = mutableMapOf<Long, MutableMap<Int, Long>>() // dayStart -> hourIndex -> ms

        var cursor = from
        var guard = 0
        while (cursor < to && guard++ < 24 * 400) { // guard: never loop more than ~400 days
            val hourStart = floorToHour(cursor)
            val hourEnd = hourStart + HourMillis
            val sliceEnd = minOf(to, hourEnd)
            val dayStart = startOfDay(cursor)
            val hourIndex = ((hourStart - dayStart) / HourMillis).toInt().coerceIn(0, 23)

            val hoursForDay = perDay.getOrPut(dayStart) { mutableMapOf() }
            hoursForDay[hourIndex] = (hoursForDay[hourIndex] ?: 0L) + (sliceEnd - cursor)

            cursor = sliceEnd
        }

        for ((dayStart, hours) in perDay) {
            val dayTotal = hours.values.sum()
            val payload = mapOf(
                "deviceId" to deviceId,
                "date" to dateFormat.format(Date(dayStart)),
                "dayStart" to dayStart,
                "totalMs" to FieldValue.increment(dayTotal),
                "hours" to hours.entries.associate { (hour, ms) -> hour.toString() to FieldValue.increment(ms) }
            )
            db.collection(DailyCollection)
                .document(dailyDocId(deviceId, dayStart))
                .set(payload, SetOptions.merge())
                .await()
        }
    }

    private fun dailyDocId(deviceId: String, dayStart: Long) =
        "${deviceId}_${dateFormat.format(Date(dayStart))}"

    // ---------------------------------------------------------------- reading

    /**
     * Every hour of recorded ON time overlapping [from, to), across all devices.
     *
     * Reads the daily documents covering the range (one single-field range query —
     * no composite index required), then adds any still-open session so a device
     * that is on right now contributes its time up to `now` instead of appearing
     * idle until it's switched off.
     */
    suspend fun loadSlices(from: Long, to: Long): List<UsageSlice> {
        val slices = mutableListOf<UsageSlice>()

        runCatching {
            db.collection(DailyCollection)
                .whereGreaterThanOrEqualTo("dayStart", startOfDay(from))
                .whereLessThanOrEqualTo("dayStart", to)
                .get().await()
                .documents.forEach { doc ->
                    val deviceId = doc.getString("deviceId") ?: return@forEach
                    val dayStart = doc.getLong("dayStart") ?: return@forEach
                    @Suppress("UNCHECKED_CAST")
                    val hours = doc.get("hours") as? Map<String, Any?> ?: return@forEach
                    hours.forEach { (hourKey, raw) ->
                        val hour = hourKey.toIntOrNull() ?: return@forEach
                        val ms = (raw as? Number)?.toLong() ?: return@forEach
                        if (ms > 0) slices += UsageSlice(deviceId, dayStart + hour * HourMillis, ms)
                    }
                }
        }.onSuccess { _lastError.value = null }
            .onFailure {
                Log.e(TAG, "loadSlices failed: ${it.message}")
                _lastError.value = it.message
            }

        slices += openSessionSlices(to)
        return slices
    }

    /** Time accumulated by sessions that are still open (device currently ON), which
     * by definition hasn't been written to a daily document yet. */
    private suspend fun openSessionSlices(to: Long): List<UsageSlice> = runCatching {
        val now = minOf(System.currentTimeMillis(), to)
        db.collection(StateCollection).get().await().documents.flatMap { doc ->
            val deviceId = doc.id
            val on = doc.getBoolean("on") ?: false
            // Unwrapped into a non-null Long here rather than relying on a smart cast:
            // `var cursor = since` would otherwise take the declared type (Long?) and
            // every arithmetic use of it below fails to compile.
            val since: Long = doc.getLong("since") ?: return@flatMap emptyList<UsageSlice>()
            if (!on || since >= now) return@flatMap emptyList<UsageSlice>()

            val result = mutableListOf<UsageSlice>()
            var cursor: Long = since
            var guard = 0
            while (cursor < now && guard++ < 24 * 400) {
                val hourStart = floorToHour(cursor)
                val sliceEnd = minOf(now, hourStart + HourMillis)
                result += UsageSlice(deviceId, hourStart, sliceEnd - cursor)
                cursor = sliceEnd
            }
            result
        }
    }.onFailure { Log.e(TAG, "openSessionSlices failed: ${it.message}") }
        .getOrElse { emptyList() }

    // ------------------------------------------------------------- aggregating

    /** Total ON milliseconds across the given slices. */
    fun totalMs(slices: List<UsageSlice>): Long = slices.sumOf { it.ms }

    /** ON milliseconds falling inside [bucketStart, bucketEnd). Slices are whole
     * clock hours, and every chart bucket is a multiple of an hour, so a slice
     * belongs wholly to one bucket — decided by where the hour starts. */
    fun msInBucket(slices: List<UsageSlice>, bucketStart: Long, bucketEnd: Long): Long =
        slices.filter { it.hourStart in bucketStart until bucketEnd }.sumOf { it.ms }

    // ------------------------------------------------------------------ time

    private fun floorToHour(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
