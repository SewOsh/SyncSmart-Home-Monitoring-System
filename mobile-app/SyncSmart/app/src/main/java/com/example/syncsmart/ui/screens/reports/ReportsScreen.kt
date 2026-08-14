package com.example.syncsmart.ui.screens.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.data.DeviceCatalog
import com.example.syncsmart.data.DeviceInfo
import com.example.syncsmart.data.DeviceKind
import com.example.syncsmart.data.UsageRepository
import com.example.syncsmart.data.UsageSlice
import com.example.syncsmart.data.estimatedWatts
import com.example.syncsmart.ui.components.BottomNav
import com.example.syncsmart.ui.components.CardBorder
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.NavTab
import com.example.syncsmart.ui.components.drawBulbFilled
import com.example.syncsmart.ui.components.drawCalendar
import com.example.syncsmart.ui.components.drawCamera
import com.example.syncsmart.ui.components.drawChevronLeft
import com.example.syncsmart.ui.components.drawChevronRight
import com.example.syncsmart.ui.components.drawFlame
import com.example.syncsmart.ui.components.drawGrid2x2
import com.example.syncsmart.ui.components.drawOutlet
import com.example.syncsmart.ui.components.drawPerson
import com.example.syncsmart.ui.components.drawSnowflake
import com.example.syncsmart.ui.theme.BodyMutedStyle
import com.example.syncsmart.ui.theme.BodyStyle
import com.example.syncsmart.ui.theme.ButtonBlue
import com.example.syncsmart.ui.theme.CaptionStyle
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.CardTitleStyle
import com.example.syncsmart.ui.theme.Danger
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.RadiusButton
import com.example.syncsmart.ui.theme.RadiusCard
import com.example.syncsmart.ui.theme.RadiusChip
import com.example.syncsmart.ui.theme.RadiusPill
import com.example.syncsmart.ui.theme.ScreenTitleStyle
import com.example.syncsmart.ui.theme.SectionTitleStyle
import com.example.syncsmart.ui.theme.SurfaceInset
import com.example.syncsmart.ui.theme.SyncSmartTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val GridLineColor = Color(0xFF223258)

private enum class ReportPeriod { DAILY, WEEKLY, MONTHLY }

/** [from, to) millis for the whole period, the bucket edges to slice the chart
 * into, and the labels/date-range text that go with those buckets. */
private data class PeriodRange(
    val from: Long,
    val to: Long,
    val buckets: List<Pair<Long, Long>>,
    val bucketLabels: List<String>,
    val dateRangeLabel: String
)

/** How long a device was on, from its daily aggregate documents in Firestore. */
private data class DeviceUsage(val device: DeviceInfo, val slices: List<UsageSlice>) {
    val totalMs: Long get() = UsageRepository.totalMs(slices)
}

private val dayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

private fun startOfToday(): Calendar = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
}

private fun periodRangeFor(period: ReportPeriod): PeriodRange {
    val today = startOfToday()
    return when (period) {
        ReportPeriod.DAILY -> {
            val from = today.timeInMillis
            val to = from + 24L * 3_600_000
            val labels = listOf("12 AM", "4 AM", "8 AM", "12 PM", "4 PM", "8 PM")
            val buckets = (0 until 6).map { i -> (from + i * 4L * 3_600_000) to (from + (i + 1) * 4L * 3_600_000) }
            PeriodRange(from, to, buckets, labels, dayFormat.format(Date(from)))
        }
        ReportPeriod.WEEKLY -> {
            val weekCal = today.clone() as Calendar
            // Calendar.DAY_OF_WEEK: Sunday=1..Saturday=7 — roll back to this week's Monday.
            val daysSinceMonday = (weekCal.get(Calendar.DAY_OF_WEEK) + 5) % 7
            weekCal.add(Calendar.DAY_OF_MONTH, -daysSinceMonday)
            val from = weekCal.timeInMillis
            val to = from + 7L * 24 * 3_600_000
            val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val buckets = (0 until 7).map { i -> (from + i * 24L * 3_600_000) to (from + (i + 1) * 24L * 3_600_000) }
            PeriodRange(from, to, buckets, labels, "${dayFormat.format(Date(from))} – ${dayFormat.format(Date(to - 1))}")
        }
        ReportPeriod.MONTHLY -> {
            val monthCal = today.clone() as Calendar
            monthCal.set(Calendar.DAY_OF_MONTH, 1)
            val from = monthCal.timeInMillis
            val daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val to = from + daysInMonth * 24L * 3_600_000
            val labels = listOf("Week 1", "Week 2", "Week 3", "Week 4")
            val buckets = (0 until 4).map { i ->
                val start = from + i * 7L * 24 * 3_600_000
                val end = if (i == 3) to else from + (i + 1) * 7L * 24 * 3_600_000
                start to end
            }
            PeriodRange(from, to, buckets, labels, "${dayFormat.format(Date(from))} – ${dayFormat.format(Date(to - 1))}")
        }
    }
}

/** Estimated kWh for one bucket, summed across every device — watts × on-hours
 * ÷ 1000 per device, since Firebase never stores real power draw. */
private fun bucketKwh(bucket: Pair<Long, Long>, usages: List<DeviceUsage>): Float {
    val wattMs = usages.sumOf { usage ->
        UsageRepository.msInBucket(usage.slices, bucket.first, bucket.second) * usage.device.kind.estimatedWatts()
    }
    val wattHours = wattMs / 3_600_000.0
    return (wattHours / 1000.0).toFloat()
}

private fun formatDuration(ms: Long): String {
    val totalMinutes = ms / 60_000
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}m"
        h > 0 -> "${h}h"
        else -> "${m}m"
    }
}

private fun DeviceKind.categoryLabel(): String = when (this) {
    DeviceKind.BULB -> "Lighting"
    DeviceKind.OUTLET -> "Outlet"
    DeviceKind.SWITCH -> "Switch"
    DeviceKind.FLAME -> "Safety"
    DeviceKind.CAMERA -> "Camera"
    DeviceKind.AC -> "Climate"
}

private fun DrawScope.drawKindIcon(kind: DeviceKind, color: Color) {
    when (kind) {
        DeviceKind.BULB -> drawBulbFilled(color)
        DeviceKind.OUTLET -> drawOutlet(color)
        DeviceKind.SWITCH -> drawGrid2x2(color)
        DeviceKind.FLAME -> drawFlame(color)
        DeviceKind.CAMERA -> drawCamera(color)
        DeviceKind.AC -> drawSnowflake(color)
    }
}

/**
 * Reports screen: date-range shortcut cards, a Daily/Weekly/Monthly toggle
 * driving a hand-drawn bar chart, and a per-device usage breakdown — all
 * computed from the real on/off event log in Firebase (see UsageRepository),
 * not mock numbers. kWh is an estimate (DeviceKind.estimatedWatts() × real
 * on-time) since nothing in this project actually measures power draw.
 *
 * The top-bar avatar is a hand-drawn placeholder — no real user photo asset
 * was provided (same gap as the Floor Plan screen's avatar).
 */
@Composable
fun ReportsScreen(
    userInitial: String = "A",
    onBack: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onDeviceRowClick: (String) -> Unit = {},
    onDashboardTabClick: () -> Unit = {},
    onFloorsTabClick: () -> Unit = {},
    onDevicesTabClick: () -> Unit = {},
    onReportsTabClick: () -> Unit = {},
    onSettingsTabClick: () -> Unit = {}
) {
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.DAILY) }
    val range = remember(selectedPeriod) { periodRangeFor(selectedPeriod) }

    var isLoading by remember { mutableStateOf(true) }
    var usages by remember { mutableStateOf<List<DeviceUsage>>(emptyList()) }
    val usageError by UsageRepository.lastError.collectAsState()

    LaunchedEffect(range) {
        isLoading = true
        // A single range query returns every device's usage for the period, so this
        // no longer fans out into one request per device.
        val slicesByDevice = UsageRepository.loadSlices(range.from, range.to).groupBy { it.deviceId }
        usages = DeviceCatalog.devices.map { device ->
            DeviceUsage(device, slicesByDevice[device.id].orEmpty())
        }
        isLoading = false
    }

    val chartValues = remember(range, usages) { range.buckets.map { bucketKwh(it, usages) } }
    val chartMax = remember(chartValues) { (chartValues.maxOrNull() ?: 0f).let { if (it <= 0f) 12f else it * 1.25f } }
    val usedDevices = remember(usages) { usages.filter { it.totalMs > 0 }.sortedByDescending { it.totalMs } }
    val maxDeviceMs = remember(usedDevices) { usedDevices.maxOfOrNull { it.totalMs }?.coerceAtLeast(1) ?: 1 }

    Scaffold(
        containerColor = NavyBg,
        bottomBar = {
            BottomNav(
                selected = NavTab.REPORTS,
                onDashboardClick = onDashboardTabClick,
                onFloorsClick = onFloorsTabClick,
                onDevicesClick = onDevicesTabClick,
                onReportsClick = onReportsTabClick,
                onSettingsClick = onSettingsTabClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Top bar.
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) { drawChevronLeft(Color.White) }
                    }
                    Text(
                        text = "Reports",
                        style = ScreenTitleStyle,
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SurfaceInset)
                            .border(2.dp, IconCyan.copy(alpha = 0.6f), CircleShape)
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(26.dp)) { drawPerson(IconCyan) }
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(RadiusChip))
                            .border(1.dp, IconCyan.copy(alpha = 0.5f), RoundedCornerShape(RadiusChip)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userInitial,
                            style = CardTitleStyle.copy(fontSize = 18.sp),
                            color = IconCyan
                        )
                    }
                }
            }

            // Date-range shortcut cards.
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DateRangeCard(
                    modifier = Modifier.weight(1f),
                    title = "Today",
                    subtitle = dayFormat.format(Date(startOfToday().timeInMillis)),
                    active = selectedPeriod == ReportPeriod.DAILY,
                    onClick = { selectedPeriod = ReportPeriod.DAILY }
                )
                DateRangeCard(
                    modifier = Modifier.weight(1f),
                    title = "This week",
                    subtitle = remember { periodRangeFor(ReportPeriod.WEEKLY).dateRangeLabel },
                    active = selectedPeriod == ReportPeriod.WEEKLY,
                    onClick = { selectedPeriod = ReportPeriod.WEEKLY }
                )
                DateRangeCard(
                    modifier = Modifier.weight(1f),
                    title = "This month",
                    subtitle = remember { periodRangeFor(ReportPeriod.MONTHLY).dateRangeLabel },
                    active = selectedPeriod == ReportPeriod.MONTHLY,
                    onClick = { selectedPeriod = ReportPeriod.MONTHLY }
                )
            }

            // Daily / Weekly / Monthly segmented toggle.
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PeriodTab(modifier = Modifier.weight(1f), label = "Daily", active = selectedPeriod == ReportPeriod.DAILY) {
                    selectedPeriod = ReportPeriod.DAILY
                }
                PeriodTab(modifier = Modifier.weight(1f), label = "Weekly", active = selectedPeriod == ReportPeriod.WEEKLY) {
                    selectedPeriod = ReportPeriod.WEEKLY
                }
                PeriodTab(modifier = Modifier.weight(1f), label = "Monthly", active = selectedPeriod == ReportPeriod.MONTHLY) {
                    selectedPeriod = ReportPeriod.MONTHLY
                }
            }

            // Chart card.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(RadiusCard))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
                    .padding(20.dp)
            ) {
                Text(
                    text = when (selectedPeriod) {
                        ReportPeriod.DAILY -> "Usage Overview (Today)"
                        ReportPeriod.WEEKLY -> "Usage Overview (This week)"
                        ReportPeriod.MONTHLY -> "Usage Overview (This month)"
                    },
                    style = CardTitleStyle,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(18.dp))
                if (isLoading) {
                    Text(text = "Loading usage…", style = BodyMutedStyle, color = MutedText)
                    Spacer(modifier = Modifier.height(172.dp))
                } else {
                    UsageBarChart(values = chartValues, maxValue = chartMax, xLabels = range.bucketLabels)
                }
            }

            // Usage by device.
            Text(
                text = "Usage by device",
                style = SectionTitleStyle,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
            if (usageError != null) {
                // Almost always Firestore asking for a composite index (deviceId + at).
                // The message carries a console link — surfacing it here is the
                // difference between a 30-second fix and a silent empty chart.
                Text(
                    text = "Couldn't load usage: $usageError",
                    style = BodyMutedStyle,
                    color = Danger
                )
            } else if (!isLoading && usedDevices.isEmpty()) {
                Text(
                    text = "No usage recorded yet for this period — usage history starts building up once devices are switched through the app.",
                    style = BodyMutedStyle,
                    color = MutedText
                )
            }
            usedDevices.forEachIndexed { index, usage ->
                DeviceUsageCard(
                    name = usage.device.label,
                    category = usage.device.kind.categoryLabel(),
                    duration = formatDuration(usage.totalMs),
                    progress = usage.totalMs.toFloat() / maxDeviceMs.toFloat(),
                    onClick = { onDeviceRowClick(usage.device.id) }
                ) { drawKindIcon(usage.device.kind, IconCyan) }
                if (index != usedDevices.lastIndex) Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DateRangeCard(modifier: Modifier = Modifier, title: String, subtitle: String, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(RadiusCard))
            .background(if (active) Color(0x1A3FC1F0) else CardBg)
            .border(1.dp, if (active) IconCyan else CardBorder, RoundedCornerShape(RadiusCard))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(RadiusChip))
                .background(ButtonBlue),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(18.dp)) { drawCalendar(Color.White) }
        }
        Text(
            text = title,
            style = CaptionStyle.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = subtitle,
            style = CaptionStyle,
            color = MutedText,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun PeriodTab(modifier: Modifier = Modifier, label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(RadiusButton))
            .background(if (active) Brush.horizontalGradient(listOf(ButtonBlue, IconCyan)) else Brush.horizontalGradient(listOf(CardBg, CardBg)))
            .border(1.dp, if (active) Color.Transparent else CardBorder, RoundedCornerShape(RadiusButton))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = BodyStyle.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.Medium, fontSize = 16.sp),
            color = if (active) Color.White else MutedText
        )
    }
}

@Composable
private fun UsageBarChart(values: List<Float>, maxValue: Float, xLabels: List<String>) {
    val step = maxValue / 4f
    val yLabels = (4 downTo 0).map { String.format(Locale.US, "%.1f kWh", step * it) }

    Row(modifier = Modifier.fillMaxWidth().height(190.dp)) {
        Column(
            modifier = Modifier.width(64.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            yLabels.forEach { label ->
                Text(text = label, style = CaptionStyle, color = MutedText)
            }
        }
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 10.dp)
        ) {
            val w = size.width
            val h = size.height
            val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            for (i in 0..4) {
                val y = h * (i / 4f)
                drawLine(GridLineColor, Offset(0f, y), Offset(w, y), strokeWidth = 1.5f, pathEffect = dash)
            }
            val slot = w / values.size
            val barWidth = slot * 0.42f
            values.forEachIndexed { index, value ->
                val ratio = (value / maxValue).coerceIn(0f, 1f)
                val barHeight = h * ratio
                if (barHeight > 1f) {
                    val left = slot * index + (slot - barWidth) / 2f
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(IconCyan, ButtonBlue),
                            startY = h - barHeight,
                            endY = h
                        ),
                        topLeft = Offset(left, h - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(barWidth * 0.25f)
                    )
                }
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(start = 74.dp, top = 8.dp)) {
        xLabels.forEach { label ->
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = label, style = CaptionStyle, color = MutedText)
            }
        }
    }
}

@Composable
private fun DeviceUsageCard(
    name: String,
    category: String,
    duration: String,
    progress: Float,
    onClick: () -> Unit,
    icon: DrawScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0x1A3FC1F0))
                    .border(1.dp, IconCyan.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(26.dp)) { icon() }
            }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(text = name, style = CardTitleStyle, color = Color.White)
                Text(text = category, style = BodyMutedStyle, color = MutedText, modifier = Modifier.padding(top = 2.dp))
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = duration, style = CardTitleStyle, color = Color.White)
                    Canvas(modifier = Modifier.padding(start = 6.dp).size(16.dp)) { drawChevronRight(MutedText) }
                }
                Text(text = "Usage time", style = CaptionStyle, color = MutedText)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(RadiusPill))
                .background(SurfaceInset)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(RadiusPill))
                    .background(Brush.horizontalGradient(listOf(ButtonBlue, IconCyan)))
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun ReportsScreenPreview() {
    SyncSmartTheme {
        ReportsScreen()
    }
}
