package com.example.syncsmart.ui.screens.device

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.R
import com.example.syncsmart.data.DeviceCatalog
import com.example.syncsmart.data.DeviceKind
import com.example.syncsmart.data.DevicesRepository
import com.example.syncsmart.ui.components.CardBorder
import com.example.syncsmart.ui.components.CircleIconButton
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.drawBulbFilled
import com.example.syncsmart.ui.components.drawCalendarClock
import com.example.syncsmart.ui.components.drawCamera
import com.example.syncsmart.ui.components.drawChevronLeft
import com.example.syncsmart.ui.components.drawExpandArrows
import com.example.syncsmart.ui.components.drawFlame
import com.example.syncsmart.ui.components.drawGear
import com.example.syncsmart.ui.components.drawGrid2x2
import com.example.syncsmart.ui.components.drawOutlet
import com.example.syncsmart.ui.components.drawPlayTriangle
import com.example.syncsmart.ui.components.drawSave
import com.example.syncsmart.ui.components.drawSnowflake
import com.example.syncsmart.ui.components.drawThreeDotsVertical
import com.example.syncsmart.ui.theme.Accent
import com.example.syncsmart.ui.theme.BodyMutedStyle
import com.example.syncsmart.ui.theme.BodyStyle
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.CaptionStyle
import com.example.syncsmart.ui.theme.CardTitleStyle
import com.example.syncsmart.ui.theme.Danger
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.RadiusButton
import com.example.syncsmart.ui.theme.RadiusCard
import com.example.syncsmart.ui.theme.RadiusChip
import com.example.syncsmart.ui.theme.RadiusMedia
import com.example.syncsmart.ui.theme.RadiusPill
import com.example.syncsmart.ui.theme.ScreenTitleStyle
import com.example.syncsmart.ui.theme.SurfaceInset
import com.example.syncsmart.ui.theme.SyncSmartTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val GoldGlow = Color(0xFFFFC94D)
private val ToggleGradient = listOf(Color(0xFF3B82F6), Color(0xFF22D3EE))
private val SaveGradient = listOf(Color(0xFF2563EB), Color(0xFF14B8A6))

/**
 * Device Control screen: device status header (bound to the real device's
 * live boolean state), plus one conditional sub-panel depending on the
 * device's kind — a 60-minute auto shut-off timer for the Iron Socket, or
 * a live camera preview for the Front Door Camera. Everything else (a plain
 * bulb, outlet, switch or AC unit) only needs the status toggle.
 *
 * The main toggle is local UI state until "Save changes" is tapped, at which
 * point it's written to Firebase via DevicesRepository. The auto shut-off
 * toggle writes immediately — it's its own safety action, not something you'd
 * want silently discarded if you navigate away without hitting Save.
 */
@Composable
fun DeviceControlScreen(
    deviceId: String = DeviceCatalog.DefaultDeviceId,
    onBack: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onExpandVideoClick: () -> Unit = {},
    onCameraSettingsClick: () -> Unit = {},
    onSaveChanges: () -> Unit = {}
) {
    val info = remember(deviceId) { DeviceCatalog.byId(deviceId) }
    val roomName = info?.roomName ?: "Unknown room"
    val deviceName = info?.label ?: deviceId
    val kind = info?.kind

    val scope = rememberCoroutineScope()
    val liveState by remember(deviceId) { DevicesRepository.observeDeviceState(deviceId) }.collectAsState(initial = null)
    val firebaseError by DevicesRepository.lastError.collectAsState()
    val isConnected by remember { DevicesRepository.observeConnected() }.collectAsState(initial = null)
    // Defaults to false (not true) so a device never *reads* as ON before we've
    // actually heard back from Firebase — the toggle stays disabled until then too.
    var deviceOn by remember(deviceId) { mutableStateOf(false) }
    var loadedFromLive by remember(deviceId) { mutableStateOf(false) }
    var isSaving by remember(deviceId) { mutableStateOf(false) }
    var loadTimedOut by remember(deviceId) { mutableStateOf(false) }
    LaunchedEffect(liveState) {
        if (!loadedFromLive && liveState != null) {
            deviceOn = liveState == true
            loadedFromLive = true
        }
    }
    LaunchedEffect(deviceId) {
        delay(10000)
        if (!loadedFromLive) loadTimedOut = true
    }
    // True the moment the toggle differs from what's actually saved in Firebase —
    // flipping the switch alone doesn't touch the database, only "Save changes" does.
    val hasPendingChange = loadedFromLive && deviceOn != (liveState == true)

    Scaffold(
        containerColor = NavyBg,
        bottomBar = {
            // navigationBarsPadding() keeps the button clear of the system gesture/
            // 3-button nav bar on edge-to-edge devices (same fix as BottomNav.kt).
            Box(modifier = Modifier.fillMaxWidth().background(NavyBg).navigationBarsPadding().padding(20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(RadiusButton))
                        .background(Brush.horizontalGradient(SaveGradient))
                        .clickable(enabled = !isSaving, onClick = {
                            isSaving = true
                            scope.launch {
                                // Only now does the toggle actually reach Firebase —
                                // flipping it earlier only changed local UI state.
                                DevicesRepository.setDeviceState(deviceId, deviceOn)
                                isSaving = false
                                onSaveChanges()
                            }
                        }),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) { drawSave(Color.White) }
                    Text(
                        text = if (isSaving) "Saving…" else "Save changes",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = Color.White,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
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
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = "Device control",
                            style = ScreenTitleStyle,
                            color = Color.White
                        )
                        Text(
                            text = "$roomName – $deviceName",
                            style = BodyStyle,
                            color = MutedText,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                CircleIconButton(onClick = onMenuClick) { drawThreeDotsVertical(Color.White) }
            }

            // Device status card — bound to this device's live Firebase state.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .clip(RoundedCornerShape(RadiusCard))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(GoldGlow.copy(alpha = 0.18f))
                        .border(2.dp, GoldGlow.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(28.dp)) { drawKindIcon(kind, GoldGlow) }
                }
                Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                    Text(text = deviceName, style = CardTitleStyle.copy(fontSize = 18.sp), color = Color.White)
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Text(text = "Status: ", style = BodyMutedStyle, color = MutedText)
                        Text(
                            text = if (!loadedFromLive) "—" else if (deviceOn) "ON" else "OFF",
                            style = BodyMutedStyle.copy(fontWeight = FontWeight.Bold),
                            color = if (loadedFromLive && deviceOn) Accent else MutedText
                        )
                    }
                    val showLoadError = !loadedFromLive && (loadTimedOut || firebaseError != null)
                    // A write that failed must never hide behind "Unsaved change" —
                    // surface the real Firestore message whenever there is one,
                    // whether it happened during the initial load or on save.
                    Text(
                        text = when {
                            firebaseError != null -> "Firebase error: $firebaseError"
                            showLoadError && isConnected == false -> "No connection to Firebase — check your internet/Wi-Fi"
                            showLoadError -> "Connected, but got no data back for \"$deviceId\" — check that field exists on /Devices/devices in Cloud Firestore"
                            !loadedFromLive -> "Loading live state…"
                            isSaving -> "Saving to Firebase…"
                            hasPendingChange -> "Unsaved change — tap Save changes"
                            else -> "Synced with Firebase"
                        },
                        style = CaptionStyle,
                        color = when {
                            firebaseError != null || showLoadError -> Danger
                            hasPendingChange && !isSaving -> GoldGlow
                            else -> MutedText
                        },
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(RadiusChip))
                            .border(1.dp, CardBorder, RoundedCornerShape(RadiusChip))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(text = kind?.name ?: "DEVICE", style = CaptionStyle.copy(fontWeight = FontWeight.Bold), color = MutedText)
                    }
                    ToggleSwitch(
                        checked = deviceOn,
                        onCheckedChange = { deviceOn = it },
                        enabled = loadedFromLive,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }

            // Safety-critical auto shut-off — only the Iron Socket has this today.
            if (deviceId == DeviceCatalog.IronSocketId) {
                AutoOffCard(deviceId = deviceId)
            }

            // Live camera preview — only camera-kind devices.
            if (kind == DeviceKind.CAMERA) {
                SubPanel(tag = "LIVE CAMERA", modifierTop = 16.dp, contentPadding = 0.dp) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 2.dp)
                            .aspectRatio(1.6f)
                            .clip(RoundedCornerShape(RadiusMedia))
                    ) {
                        Image(
                            painter = painterResource(R.drawable.living_room_camera),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Subtle scrim so the white play/live/expand/gear overlays stay legible.
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.12f)))

                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0x33FFFFFF))
                                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(20.dp)) { drawPlayTriangle(Color.White) }
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Live", style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium), color = Color.White)
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Accent)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Canvas(modifier = Modifier
                                .size(20.dp)
                                .clickable(onClick = onExpandVideoClick)
                            ) { drawExpandArrows(Color.White) }
                            Canvas(modifier = Modifier
                                .size(20.dp)
                                .clickable(onClick = onCameraSettingsClick)
                            ) { drawGear(Color.White) }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawKindIcon(kind: DeviceKind?, color: Color) {
    when (kind) {
        DeviceKind.BULB, null -> drawBulbFilled(color)
        DeviceKind.OUTLET -> drawOutlet(color)
        DeviceKind.SWITCH -> drawGrid2x2(color)
        DeviceKind.FLAME -> drawFlame(color)
        DeviceKind.CAMERA -> drawCamera(color)
        DeviceKind.AC -> drawSnowflake(color)
    }
}

private const val AutoOffDurationMillis = 60L * 60L * 1000L // 60 minutes

/**
 * Safety timer for the Iron Socket: a toggle that, when switched on, writes a
 * "turn off at" timestamp (now + 60 min) to Firebase and counts down live.
 * When the countdown reaches zero the app itself calls setDeviceState(false)
 * and clears the timer — this only fires while the app is open; it's a
 * software safety net, not a hardware-level guarantee (see chat notes).
 */
@Composable
private fun AutoOffCard(deviceId: String) {
    val scope = rememberCoroutineScope()
    val autoOffAt by remember(deviceId) { DevicesRepository.observeAutoOffAt(deviceId) }.collectAsState(initial = null)
    var remainingMs by remember(deviceId) { mutableStateOf<Long?>(null) }

    LaunchedEffect(autoOffAt) {
        val target = autoOffAt
        if (target == null) {
            remainingMs = null
        } else {
            while (true) {
                val left = target - System.currentTimeMillis()
                if (left <= 0) {
                    remainingMs = 0
                    DevicesRepository.setDeviceState(deviceId, false)
                    DevicesRepository.setAutoOffAt(deviceId, null)
                    break
                }
                remainingMs = left
                delay(1000)
            }
        }
    }

    SubPanel(tag = "AUTO SHUT-OFF", modifierTop = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(30.dp)) { drawCalendarClock(IconCyan) }
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(text = "60-min auto shut-off", style = CardTitleStyle, color = Color.White)
                Text(
                    text = when {
                        autoOffAt == null -> "Off — stays on until you switch it off"
                        (remainingMs ?: 0L) > 0L -> "Turns off in ${formatCountdown(remainingMs ?: 0L)}"
                        else -> "Turning off…"
                    },
                    style = BodyMutedStyle,
                    color = if (autoOffAt != null) GoldGlow else MutedText,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            ToggleSwitch(
                checked = autoOffAt != null,
                onCheckedChange = { enabled ->
                    scope.launch {
                        DevicesRepository.setAutoOffAt(
                            deviceId,
                            if (enabled) System.currentTimeMillis() + AutoOffDurationMillis else null
                        )
                    }
                }
            )
        }
    }
}

private fun formatCountdown(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun SubPanel(
    tag: String,
    modifierTop: androidx.compose.ui.unit.Dp = 0.dp,
    contentPadding: androidx.compose.ui.unit.Dp = 18.dp,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = modifierTop)
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
            .padding(top = 18.dp, bottom = if (contentPadding == 0.dp) 0.dp else 18.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 18.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(RadiusChip))
                    .border(1.dp, IconCyan.copy(alpha = 0.4f), RoundedCornerShape(RadiusChip))
                    .background(Color(0x1A3FC1F0))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = tag,
                    style = CaptionStyle.copy(fontWeight = FontWeight.Bold),
                    color = IconCyan
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(horizontal = if (contentPadding == 0.dp) 0.dp else 18.dp)
                .padding(top = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun RowDivider(topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
            .height(1.dp)
            .background(CardBorder)
    )
}

@Composable
private fun ToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .width(50.dp)
            .height(28.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(RadiusPill))
            .background(
                if (checked) Brush.horizontalGradient(ToggleGradient) else Brush.horizontalGradient(listOf(SurfaceInset, SurfaceInset))
            )
            .border(1.dp, if (checked) Color.Transparent else CardBorder, RoundedCornerShape(RadiusPill))
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(3.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (checked) Color.White else Color(0xFFAEB8CC))
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1200)
@Composable
private fun DeviceControlScreenPreview() {
    SyncSmartTheme {
        DeviceControlScreen()
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1200, name = "Iron socket (auto shut-off)")
@Composable
private fun DeviceControlScreenIronSocketPreview() {
    SyncSmartTheme {
        DeviceControlScreen(deviceId = DeviceCatalog.IronSocketId)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1200, name = "Front door camera")
@Composable
private fun DeviceControlScreenCameraPreview() {
    SyncSmartTheme {
        DeviceControlScreen(deviceId = "G03_front_door_camera")
    }
}
