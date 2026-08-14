package com.example.syncsmart.ui.screens.devices

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.data.DeviceCatalog
import com.example.syncsmart.data.DeviceInfo
import com.example.syncsmart.data.DeviceKind
import com.example.syncsmart.data.DevicesRepository
import com.example.syncsmart.ui.components.BottomNav
import com.example.syncsmart.ui.components.CardBorder
import com.example.syncsmart.ui.components.CircleIconButton
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.NavTab
import com.example.syncsmart.ui.components.drawBulb
import com.example.syncsmart.ui.components.drawCamera
import com.example.syncsmart.ui.components.drawChevronLeft
import com.example.syncsmart.ui.components.drawChevronRight
import com.example.syncsmart.ui.components.drawFlame
import com.example.syncsmart.ui.components.drawGrid2x2
import com.example.syncsmart.ui.components.drawOutlet
import com.example.syncsmart.ui.components.drawSnowflake
import com.example.syncsmart.ui.theme.Accent
import com.example.syncsmart.ui.theme.BodyMutedStyle
import com.example.syncsmart.ui.theme.CaptionStyle
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.CardTitleStyle
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.RadiusCard
import com.example.syncsmart.ui.theme.RadiusChip
import com.example.syncsmart.ui.theme.ScreenTitleStyle
import com.example.syncsmart.ui.theme.SurfaceInset
import com.example.syncsmart.ui.theme.SyncSmartTheme

/**
 * "Devices" tab: every real device across both floors, grouped by room, each
 * showing its live ON/OFF state from Firebase — this is what the bottom nav's
 * "Devices" tab opens now, instead of jumping straight into one hardcoded
 * device's control screen. Tapping a row opens that device's Device Control
 * screen, same as tapping its marker on the Floor Plan does.
 */
@Composable
fun DevicesListScreen(
    onBack: () -> Unit = {},
    onDeviceClick: (String) -> Unit = {},
    onDashboardTabClick: () -> Unit = {},
    onFloorsTabClick: () -> Unit = {},
    onReportsTabClick: () -> Unit = {},
    onSettingsTabClick: () -> Unit = {}
) {
    val deviceStates by remember { DevicesRepository.observeDeviceStates() }.collectAsState(initial = emptyMap())
    val onCount = deviceStates.values.count { it }
    val totalCount = DeviceCatalog.devices.size

    Scaffold(
        containerColor = NavyBg,
        bottomBar = {
            BottomNav(
                selected = NavTab.DEVICES,
                onDashboardClick = onDashboardTabClick,
                onFloorsClick = onFloorsTabClick,
                onDevicesClick = {},
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(onClick = onBack) { drawChevronLeft(Color.White) }
                Text(
                    text = "Devices",
                    style = ScreenTitleStyle,
                    color = Color.White,
                    modifier = Modifier.padding(start = 14.dp)
                )
            }
            Text(
                text = "$onCount of $totalCount devices on",
                style = BodyMutedStyle,
                color = MutedText,
                modifier = Modifier.padding(top = 6.dp, start = 2.dp)
            )

            DeviceCatalog.rooms.forEach { room ->
                val roomDevices = DeviceCatalog.devicesForRoom(room.id)
                if (roomDevices.isNotEmpty()) {
                    Text(
                        text = room.name,
                        style = CardTitleStyle,
                        color = IconCyan,
                        modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
                    )
                    roomDevices.forEach { device ->
                        DeviceRow(
                            device = device,
                            isOn = deviceStates[device.id] == true,
                            onClick = { onDeviceClick(device.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun DeviceRow(device: DeviceInfo, isOn: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isOn) Color(0x1A3FC1F0) else SurfaceInset)
                .border(1.dp, if (isOn) IconCyan.copy(alpha = 0.5f) else CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(19.dp)) { drawKindIcon(device.kind, if (isOn) IconCyan else MutedText) }
        }
        Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
            Text(text = device.label, style = CardTitleStyle.copy(fontSize = 15.sp), color = Color.White)
            Text(text = device.roomName, style = CaptionStyle, color = MutedText, modifier = Modifier.padding(top = 2.dp))
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(RadiusChip))
                .background(if (isOn) Color(0x1A3FC1F0) else SurfaceInset)
                .border(1.dp, if (isOn) IconCyan.copy(alpha = 0.4f) else CardBorder, RoundedCornerShape(RadiusChip))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = if (isOn) "ON" else "OFF",
                style = CaptionStyle.copy(fontWeight = FontWeight.Bold),
                color = if (isOn) Accent else MutedText
            )
        }
        Canvas(modifier = Modifier.padding(start = 8.dp).size(14.dp)) { drawChevronRight(MutedText) }
    }
}

private fun DrawScope.drawKindIcon(kind: DeviceKind, color: Color) {
    when (kind) {
        DeviceKind.BULB -> drawBulb(color)
        DeviceKind.OUTLET -> drawOutlet(color)
        DeviceKind.SWITCH -> drawGrid2x2(color)
        DeviceKind.FLAME -> drawFlame(color)
        DeviceKind.CAMERA -> drawCamera(color)
        DeviceKind.AC -> drawSnowflake(color)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 1400)
@Composable
private fun DevicesListScreenPreview() {
    SyncSmartTheme {
        DevicesListScreen()
    }
}
