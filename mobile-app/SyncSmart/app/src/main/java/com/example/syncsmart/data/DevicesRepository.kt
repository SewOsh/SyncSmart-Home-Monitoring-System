package com.example.syncsmart.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

private const val TAG = "DevicesRepository"

/**
 * Wraps the Cloud Firestore document that holds live device state.
 *
 * The hardware simulator stores everything in ONE document — /Devices/devices —
 * where each field is a device id (e.g. "G01_living_room_light") whose value is
 * a plain boolean on/off state. Device metadata (name/room/icon) is not stored
 * there; it lives in [DeviceCatalog] inside the app.
 *
 * The one non-boolean field is "G02_iron_socket_schedule", which holds an
 * epoch-millis "auto shut off at" timestamp (or null) — see [observeAutoOffAt].
 *
 * NOTE: this project also has an unused Realtime Database instance, and a stray
 * lowercase "devices" Firestore collection. Neither holds real data. Writing to
 * the wrong one is completely silent — the write succeeds and nothing appears to
 * change — so treat the exact path constants below as load-bearing.
 *
 * No sign-in required — the app talks to Firestore without Firebase Auth, so
 * the Firestore security rules must allow unauthenticated read/write on
 * /Devices/{doc}, /usage_daily/{doc} and /usage_state/{doc}.
 */
object DevicesRepository {

    // Case-sensitive, and it matters: the project contains BOTH "Devices" (the real
    // hardware data) and a stray lowercase "devices" collection this app created
    // earlier by writing to the wrong name. Firestore treats them as unrelated
    // collections, so writes appeared to succeed while the console never changed.
    internal const val DevicesCollection = "Devices"
    internal const val DevicesDocument = "devices"

    private val db get() = FirebaseFirestore.getInstance()
    private val devicesDoc get() = db.collection(DevicesCollection).document(DevicesDocument)

    private val _lastError = MutableStateFlow<String?>(null)

    /** The most recent Firestore read/write failure message, e.g. "PERMISSION_DENIED" —
     * shown directly on-screen so a rules/connectivity problem is visible without
     * needing Logcat. Null once something succeeds. */
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    /** Reads a stored field as a Boolean regardless of whether the hardware side
     * wrote it as a real boolean, a number (0/1) or a string ("true"/"on"/…).
     * Returns null only when the value truly can't be read as on/off. */
    private fun coerceBoolean(raw: Any?): Boolean? = when (raw) {
        is Boolean -> raw
        is Long -> raw != 0L
        is Double -> raw != 0.0
        is String -> when (raw.trim().lowercase()) {
            "true", "1", "on", "yes" -> true
            "false", "0", "off", "no" -> false
            else -> null
        }
        else -> null
    }

    private fun coerceLong(raw: Any?): Long? = when (raw) {
        is Long -> raw
        is Int -> raw.toLong()
        is Double -> raw.toLong()
        is com.google.firebase.Timestamp -> raw.toDate().time
        is String -> raw.trim().toLongOrNull()
        else -> null
    }

    /** Live snapshot of the whole devices document, as a raw field map. Every other
     * observer below is derived from this single listener. */
    private fun observeRawFields(): Flow<Map<String, Any?>> = callbackFlow {
        val registration = devicesDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // A permission-denied or network hiccup must NOT crash the app —
                // surface an empty map (markers read as DISCONNECTED) instead of
                // closing the flow with an exception.
                Log.e(TAG, "devices snapshot failed: ${error.message}")
                _lastError.value = error.message
                trySend(emptyMap())
                return@addSnapshotListener
            }
            _lastError.value = null
            trySend(snapshot?.data.orEmpty())
        }
        awaitClose { registration.remove() }
    }

    /** Live map of every boolean device field, keyed by device id. The
     * "*_schedule" fields are skipped — read those via [observeAutoOffAt]. */
    fun observeDeviceStates(): Flow<Map<String, Boolean>> = observeRawFields().map { fields ->
        fields.mapNotNull { (key, value) ->
            if (key.endsWith("_schedule")) return@mapNotNull null
            val state = coerceBoolean(value) ?: return@mapNotNull null
            key to state
        }.toMap()
    }

    /** Live boolean state for a single device. Null only means "haven't heard back
     * from Firestore at all yet" (or a real read error) — once any response
     * arrives, even for a missing/oddly-typed field, this resolves to a real
     * true/false instead of hanging forever. */
    fun observeDeviceState(deviceId: String): Flow<Boolean?> = callbackFlow {
        val registration = devicesDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "observeDeviceState($deviceId) failed: ${error.message}")
                _lastError.value = error.message
                trySend(null)
                return@addSnapshotListener
            }
            _lastError.value = null
            trySend(coerceBoolean(snapshot?.get(deviceId)) ?: false)
        }
        awaitClose { registration.remove() }
    }

    /** Writes the device's new on/off state to its field on the devices document.
     * Uses set(merge) rather than update() because update() fails outright if the
     * document doesn't exist yet, while merge writes just this one field and leaves
     * the other 13 devices' fields untouched either way. */
    suspend fun setDeviceState(deviceId: String, value: Boolean) {
        runCatching {
            devicesDoc.set(mapOf(deviceId to value), SetOptions.merge()).await()
            logUsageEvent(deviceId, value)
        }
            .onSuccess { _lastError.value = null }
            .onFailure {
                Log.e(TAG, "setDeviceState($deviceId) failed: ${it.message}")
                _lastError.value = it.message
            }
    }

    /** Banks usage time into Reports' daily aggregate documents — see
     * [UsageRepository.recordStateChange]. Deliberately does NOT create a document
     * per toggle; it updates one document per device per day. */
    private suspend fun logUsageEvent(deviceId: String, on: Boolean) {
        UsageRepository.recordStateChange(deviceId, on)
    }

    /** Epoch-millis timestamp at which a device should automatically turn off, or
     * null if no auto shut-off timer is running. Only the Iron Socket uses this
     * today (see [DeviceCatalog.IronSocketId]) — stored in the "{deviceId}_schedule"
     * field on the same devices document. */
    fun observeAutoOffAt(deviceId: String): Flow<Long?> = callbackFlow {
        val registration = devicesDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "observeAutoOffAt($deviceId) failed: ${error.message}")
                _lastError.value = error.message
                trySend(null)
                return@addSnapshotListener
            }
            _lastError.value = null
            trySend(coerceLong(snapshot?.get("${deviceId}_schedule")))
        }
        awaitClose { registration.remove() }
    }

    /** Pass null to cancel/clear the timer. */
    suspend fun setAutoOffAt(deviceId: String, atMillis: Long?) {
        runCatching { devicesDoc.set(mapOf("${deviceId}_schedule" to atMillis), SetOptions.merge()).await() }
            .onSuccess { _lastError.value = null }
            .onFailure {
                Log.e(TAG, "setAutoOffAt($deviceId) failed: ${it.message}")
                _lastError.value = it.message
            }
    }

    /** Whether the latest devices snapshot came from the server rather than
     * Firestore's offline cache — the practical equivalent of Realtime Database's
     * ".info/connected" for telling "no internet reaching Firebase" apart from
     * "rules rejected it" (which reports an error instead). */
    fun observeConnected(): Flow<Boolean> = callbackFlow {
        val registration = devicesDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(false)
                return@addSnapshotListener
            }
            trySend(snapshot != null && !snapshot.metadata.isFromCache)
        }
        awaitClose { registration.remove() }
    }
}
