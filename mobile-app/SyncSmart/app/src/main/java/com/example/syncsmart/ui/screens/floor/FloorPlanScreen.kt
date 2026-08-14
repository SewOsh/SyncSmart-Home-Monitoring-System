package com.example.syncsmart.ui.screens.floor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.data.DeviceCatalog
import com.example.syncsmart.data.DeviceKind
import com.example.syncsmart.data.DevicesRepository
import com.example.syncsmart.ui.components.BottomNav
import com.example.syncsmart.ui.components.CardBorder
import com.example.syncsmart.ui.components.CircleIconButton
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.LogoBlue
import com.example.syncsmart.ui.components.NavTab
import com.example.syncsmart.ui.components.drawBulb
import com.example.syncsmart.ui.components.drawCamera
import com.example.syncsmart.ui.components.drawChevronLeft
import com.example.syncsmart.ui.components.drawFlame
import com.example.syncsmart.ui.components.drawGrid2x2
import com.example.syncsmart.ui.components.drawInfo
import com.example.syncsmart.ui.components.drawMinus
import com.example.syncsmart.ui.components.drawOutlet
import com.example.syncsmart.ui.components.drawPerson
import com.example.syncsmart.ui.components.drawPlus
import com.example.syncsmart.ui.components.drawSnowflake
import com.example.syncsmart.ui.theme.BodyMutedStyle
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.CaptionStyle
import com.example.syncsmart.ui.theme.Danger
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.RadiusButton
import com.example.syncsmart.ui.theme.RadiusChip
import com.example.syncsmart.ui.theme.RadiusPanel
import com.example.syncsmart.ui.theme.ScreenTitleStyle
import com.example.syncsmart.ui.theme.SurfaceInset
import com.example.syncsmart.ui.theme.SyncSmartTheme

private val PanelBg = CardBg
private val OffColor = Color(0xFF5B6B95)

private enum class DeviceStatus { ON, OFF, ERROR, DISCONNECTED }

private data class RoomDevice(
    val id: String,
    val label: String,
    val kind: DeviceKind,
    val status: DeviceStatus
)

private data class RoomZone(val xMin: Float, val xMax: Float, val y: Float, val labelY: Float)

private data class Room(val id: String, val name: String, val zone: RoomZone, val devices: List<RoomDevice>)

// Matches the blueprint's three wall-divided areas: top-left, top-right, bottom
// strip. Widened close to each room's full wall-to-wall span (rather than a
// narrow mid-band) so rooms with several devices (e.g. Living Room's 5) have
// room to lay markers out without their labels colliding.
private val ZoneTopLeft = RoomZone(xMin = 0.08f, xMax = 0.48f, y = 0.34f, labelY = 0.16f)
private val ZoneTopRight = RoomZone(xMin = 0.56f, xMax = 0.94f, y = 0.34f, labelY = 0.16f)
private val ZoneBottom = RoomZone(xMin = 0.10f, xMax = 0.90f, y = 0.78f, labelY = 0.66f)

/** No more than this many device markers share one horizontal row before wrapping.
 * Two rather than three: a zone is only ~40% of the panel width, so three markers
 * left each label about 46dp — too narrow for "Living Room Light" to be readable
 * at any sensible font size. Two markers per row roughly doubles that budget. */
private const val MaxDevicesPerRow = 2

/** Vertical gap (as a fraction of panel height) between wrapped device rows. */
private const val RowSpacing = 0.13f

private fun statusColor(status: DeviceStatus): Color = when (status) {
    DeviceStatus.ON -> IconCyan
    DeviceStatus.OFF -> OffColor
    DeviceStatus.ERROR -> Danger
    DeviceStatus.DISCONNECTED -> MutedText
}

/**
 * Horizontal centre positions for [count] markers inside [zone].
 *
 * [halfSlot] is half a marker's width as a fraction of panel width, and the usable
 * span is shrunk by it at both ends. Without that inset the outermost markers are
 * centred exactly on the zone edges, so half of each badge hangs outside the zone —
 * which is what made the Living Room's right-hand marker collide with the Kitchen's
 * left-hand one, since those zones are only 0.08 apart.
 */
private fun xPositionsFor(zone: RoomZone, count: Int, halfSlot: Float = 0f): List<Float> {
    val centre = (zone.xMin + zone.xMax) / 2f
    val min = zone.xMin + halfSlot
    val max = zone.xMax - halfSlot
    if (count <= 1 || min >= max) return List(count.coerceAtLeast(1)) { centre }
    val step = (max - min) / (count - 1)
    return (0 until count).map { min + step * it }
}

/** Which blueprint zone each real room id renders in, per floor. */
private fun zoneFor(roomId: String): RoomZone = when (roomId) {
    "F101", "G01" -> ZoneTopLeft
    "F102", "G02" -> ZoneTopRight
    else -> ZoneBottom // F103, G03
}

/**
 * Real room/device structure for Ground floor and First floor, built from
 * DeviceCatalog (the static id/label/room/kind metadata) combined with each
 * device's live boolean state from Firebase. A device missing from the live
 * map yet (still loading, or never written) shows as DISCONNECTED rather
 * than guessing ON/OFF.
 */
private fun devicesForFloor(floorId: String, deviceStates: Map<String, Boolean>): List<Room> =
    DeviceCatalog.roomsForFloor(floorId).map { room ->
        val devices = DeviceCatalog.devicesForRoom(room.id).map { info ->
            RoomDevice(
                id = info.id,
                label = info.label,
                kind = info.kind,
                status = when (deviceStates[info.id]) {
                    true -> DeviceStatus.ON
                    false -> DeviceStatus.OFF
                    null -> DeviceStatus.DISCONNECTED
                }
            )
        }
        Room(id = room.id, name = room.name, zone = zoneFor(room.id), devices = devices)
    }

/**
 * Floor plan screen: back/title/avatar top bar, a bordered blueprint-style
 * panel with hand-drawn wall lines and status-colored device markers, a zoom
 * control, a status legend, an info hint banner, a gradient add-device FAB
 * and the persistent bottom nav with "Floors" active.
 *
 * The blueprint itself and the top-bar avatar are hand-drawn placeholders —
 * no real per-floor plan render or user photo was provided (same gap as the
 * Dashboard's floor cards). Swap in real assets the same way once available.
 */
@Composable
fun FloorPlanScreen(
    floorId: String,
    floorName: String,
    onBack: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onFloorSwitchClick: () -> Unit = {},
    onDeviceClick: (String) -> Unit = {},
    onAddClick: () -> Unit = {},
    onDashboardTabClick: () -> Unit = {},
    onFloorsTabClick: () -> Unit = {},
    onDevicesTabClick: () -> Unit = {},
    onReportsTabClick: () -> Unit = {},
    onSettingsTabClick: () -> Unit = {}
) {
    val deviceStates by remember { DevicesRepository.observeDeviceStates() }.collectAsState(initial = emptyMap())
    val rooms = remember(floorId, deviceStates) { devicesForFloor(floorId, deviceStates) }

    Scaffold(
        containerColor = NavyBg,
        bottomBar = {
            BottomNav(
                selected = NavTab.FLOORS,
                onDashboardClick = onDashboardTabClick,
                onFloorsClick = onFloorsTabClick,
                onDevicesClick = onDevicesTabClick,
                onReportsClick = onReportsTabClick,
                onSettingsClick = onSettingsTabClick
            )
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(IconCyan, LogoBlue)))
                    .clickable(onClick = onAddClick),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(22.dp)) { drawPlus(Color.White) }
            }
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
                    CircleIconButton(onClick = onBack) { drawChevronLeft(Color.White) }
                    Text(
                        text = floorName,
                        style = ScreenTitleStyle,
                        color = Color.White,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SurfaceInset)
                            .border(2.dp, IconCyan.copy(alpha = 0.5f), CircleShape)
                            .clickable(onClick = onProfileClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) { drawPerson(IconCyan) }
                    }
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(26.dp)
                            .clip(RoundedCornerShape(RadiusChip))
                            .background(Color(0x1A3FC1F0))
                            .border(1.dp, CardBorder, RoundedCornerShape(RadiusChip))
                            .clickable(onClick = onFloorSwitchClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = floorId.take(1).uppercase(),
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                            color = IconCyan
                        )
                    }
                }
            }

            // Blueprint panel.
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    // Taller than wide: the Living Room's 5 devices now wrap to three
                    // rows of two, which needs vertical room that a squarer panel
                    // didn't have (rows would ride up into the room-name label).
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(RadiusPanel))
                    .background(PanelBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(RadiusPanel))
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) { drawBlueprint() }

                rooms.forEach { room ->
                    val centerX = (room.zone.xMin + room.zone.xMax) / 2f
                    Box(
                        modifier = Modifier
                            .offset(x = maxWidth * centerX - 55.dp, y = maxHeight * room.zone.labelY - 9.dp)
                            .width(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = room.name,
                            style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                            color = IconCyan.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Wrap into rows of at most MaxDevicesPerRow so a crowded room
                    // (e.g. Living Room's 5 devices) never squeezes labels into a
                    // sliver too narrow to read — extra devices drop to a new row
                    // instead of overlapping their neighbors.
                    val deviceRows = room.devices.chunked(MaxDevicesPerRow)
                    val startY = room.zone.y - (deviceRows.size - 1) * RowSpacing / 2f
                    deviceRows.forEachIndexed { rowIndex, rowDevices ->
                        val rowY = startY + rowIndex * RowSpacing
                        val slotWidth = (maxWidth * (room.zone.xMax - room.zone.xMin) / rowDevices.size)
                            .coerceIn(64.dp, 88.dp)
                        // Keep whole badges inside their zone, not just their centres.
                        val xs = xPositionsFor(room.zone, rowDevices.size, halfSlot = (slotWidth / maxWidth) / 2f)
                        rowDevices.forEachIndexed { colIndex, device ->
                            Box(
                                modifier = Modifier.offset(
                                    x = maxWidth * xs[colIndex] - slotWidth / 2,
                                    y = maxHeight * rowY - 19.dp
                                )
                            ) {
                                DeviceBadge(device = device, width = slotWidth, onClick = { onDeviceClick(device.id) })
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ZoomButton { drawPlus(IconCyan) }
                    ZoomButton { drawMinus(IconCyan) }
                }
            }

            // Status legend.
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LegendItem("ON", IconCyan)
                LegendItem("OFF", OffColor)
                LegendItem("ERROR", Danger)
                LegendItem("DISCONNECTED", MutedText)
            }

            // Info hint banner.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(RadiusButton))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(RadiusButton))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x1A3FC1F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(16.dp)) { drawInfo(IconCyan) }
                }
                Text(
                    text = "Tap any marker → opens Device Control (screen 04).",
                    style = BodyMutedStyle,
                    color = MutedText,
                    modifier = Modifier.padding(start = 12.dp).weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun DeviceBadge(device: RoomDevice, width: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    val ringColor = statusColor(device.status)
    Column(
        modifier = Modifier.width(width).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(CardBg)
                .border(2.dp, ringColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(16.dp)) {
                when (device.kind) {
                    DeviceKind.BULB -> drawBulb(ringColor)
                    DeviceKind.OUTLET -> drawOutlet(ringColor)
                    DeviceKind.SWITCH -> drawGrid2x2(ringColor)
                    DeviceKind.FLAME -> drawFlame(ringColor)
                    DeviceKind.CAMERA -> drawCamera(ringColor)
                    DeviceKind.AC -> drawSnowflake(ringColor)
                }
            }
        }
        Text(
            text = device.label,
            style = TextStyle(fontSize = 9.5.sp, fontWeight = FontWeight.Medium, lineHeight = 11.sp),
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

@Composable
private fun ZoomButton(draw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(RadiusChip))
            .background(Color(0xFF10204A))
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusChip)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(16.dp)) { draw() }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .border(2.dp, color, CircleShape)
        )
        Text(
            text = label,
            style = CaptionStyle,
            color = MutedText,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

/** Hand-drawn abstract blueprint: faint dot grid + wall outline + two interior dividers. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlueprint() {
    val w = size.width
    val h = size.height
    val gridColor = IconCyan.copy(alpha = 0.10f)
    val step = w * 0.09f
    var gx = 0f
    while (gx < w) {
        var gy = 0f
        while (gy < h) {
            drawCircle(gridColor, radius = 1.4f, center = Offset(gx, gy))
            gy += step
        }
        gx += step
    }

    val wallColor = IconCyan.copy(alpha = 0.5f)
    val wallStroke = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)

    drawRoundRect(
        color = wallColor,
        topLeft = Offset(w * 0.04f, h * 0.06f),
        size = androidx.compose.ui.geometry.Size(w * 0.92f, h * 0.88f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.03f),
        style = wallStroke
    )
    drawLine(wallColor, Offset(w * 0.52f, h * 0.06f), Offset(w * 0.52f, h * 0.60f), strokeWidth = 4f)
    drawLine(wallColor, Offset(w * 0.04f, h * 0.60f), Offset(w * 0.52f, h * 0.60f), strokeWidth = 4f)
    drawLine(wallColor, Offset(w * 0.52f, h * 0.60f), Offset(w * 0.96f, h * 0.60f), strokeWidth = 4f)
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun FloorPlanScreenPreview() {
    SyncSmartTheme {
        FloorPlanScreen(floorId = "ground", floorName = "Ground floor")
    }
}
