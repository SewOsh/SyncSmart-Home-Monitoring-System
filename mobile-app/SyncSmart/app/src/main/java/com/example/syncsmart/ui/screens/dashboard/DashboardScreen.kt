package com.example.syncsmart.ui.screens.dashboard

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.R
import com.example.syncsmart.data.DeviceCatalog
import com.example.syncsmart.data.DevicesRepository
import com.example.syncsmart.ui.components.BottomNav
import com.example.syncsmart.ui.components.CardBorder
import com.example.syncsmart.ui.components.CircleIconButton
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.NavTab
import com.example.syncsmart.ui.components.drawBarChart
import com.example.syncsmart.ui.components.drawBell
import com.example.syncsmart.ui.components.drawChevronDown
import com.example.syncsmart.ui.components.drawChevronRight
import com.example.syncsmart.ui.components.drawExclamation
import com.example.syncsmart.ui.components.drawGrid2x2
import com.example.syncsmart.ui.components.drawHamburger
import com.example.syncsmart.ui.components.drawHouseOutline
import com.example.syncsmart.ui.components.drawPerson
import com.example.syncsmart.ui.components.drawWifi
import com.example.syncsmart.ui.theme.BodyMutedStyle
import com.example.syncsmart.ui.theme.CardTitleStyle
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.Danger
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.RadiusCard
import com.example.syncsmart.ui.theme.RadiusChip
import com.example.syncsmart.ui.theme.RadiusMedia
import com.example.syncsmart.ui.theme.ScreenTitleStyle
import com.example.syncsmart.ui.theme.SectionTitleStyle
import com.example.syncsmart.ui.theme.SyncSmartTheme

/**
 * Dashboard / home screen: top bar, house-photo greeting banner, three stat
 * cards, a recent safety alert, two floor cards and a reports row, with a
 * persistent bottom nav.
 *
 * The two floor cards use real isometric floor-render photos
 * (R.drawable.ground_floor / R.drawable.first_floor).
 */
@Composable
fun DashboardScreen(
    userName: String = "Alex",
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onHomeSwitchClick: () -> Unit = {},
    onViewAllAlertsClick: () -> Unit = {},
    onAlertClick: () -> Unit = {},
    onFloorClick: (String) -> Unit = {},
    onReportsRowClick: () -> Unit = {},
    onDashboardTabClick: () -> Unit = {},
    onFloorsTabClick: () -> Unit = {},
    onDevicesTabClick: () -> Unit = {},
    onReportsTabClick: () -> Unit = {},
    onSettingsTabClick: () -> Unit = {}
) {
    // Live device state straight from Firestore, same source the Floor Plan and
    // Devices list use. Null until the first snapshot arrives, so the card shows
    // "—" rather than a made-up count while loading.
    val deviceStates by remember { DevicesRepository.observeDeviceStates() }.collectAsState(initial = null)
    val devicesOn = deviceStates?.values?.count { it }
    val roomCount = DeviceCatalog.rooms.size

    Scaffold(
        containerColor = NavyBg,
        bottomBar = {
            BottomNav(
                selected = NavTab.DASHBOARD,
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
        ) {
            // Top bar.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircleIconButton(onClick = onMenuClick) { drawHamburger(Color.White) }
                    Row(
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .clickable(onClick = onHomeSwitchClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My home",
                            style = ScreenTitleStyle,
                            color = Color.White
                        )
                        Canvas(modifier = Modifier.padding(start = 6.dp).size(16.dp)) { drawChevronDown(Color.White) }
                    }
                }
                CircleIconButton(onClick = onProfileClick) { drawPerson(Color.White) }
            }

            // Hero banner.
            Box(modifier = Modifier.fillMaxWidth().height(210.dp)) {
                Image(
                    painter = painterResource(R.drawable.img_2),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .fillMaxWidth(0.58f)
                )
                Column(modifier = Modifier.align(Alignment.TopStart).padding(horizontal = 20.dp)) {
                    Row {
                        Text(
                            text = "Good evening, ",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                            color = Color.White
                        )
                        Text(
                            text = "$userName!",
                            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
                            color = IconCyan
                        )
                    }
                    Text(
                        text = "Here's what's happening at your home.",
                        style = TextStyle(fontSize = 14.sp),
                        color = MutedText,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            // Stat cards.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = devicesOn?.toString() ?: "—",
                    label = "Devices on"
                ) { drawWifi(IconCyan) }
                StatCard(modifier = Modifier.weight(1f), value = "2", label = "Alerts") { drawBell(IconCyan) }
                StatCard(
                    modifier = Modifier.weight(1f),
                    value = roomCount.toString(),
                    label = "Rooms"
                ) { drawHouseOutline(IconCyan) }
            }

            // Recent safety alerts.
            SectionHeader(title = "Recent safety alerts", actionText = "View all", onActionClick = onViewAllAlertsClick)
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)) {
                SafetyAlertCard(
                    title = "Iron Socket (Kitchen) overheated",
                    subtitle = "Safety device activated",
                    timeAgo = "2m ago",
                    onClick = onAlertClick
                )
            }

            // Floors.
            SectionHeader(title = "Floors", actionText = null, onActionClick = {})
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FloorCard(
                    modifier = Modifier.weight(1f),
                    name = "Ground floor",
                    subtitle = floorSubtitle("ground"),
                    imageRes = R.drawable.ground_floor,
                    onClick = { onFloorClick("ground") }
                )
                FloorCard(
                    modifier = Modifier.weight(1f),
                    name = "First floor",
                    subtitle = floorSubtitle("first"),
                    imageRes = R.drawable.first_floor,
                    onClick = { onFloorClick("first") }
                )
            }

            // Reports row.
            Box(modifier = Modifier.padding(20.dp)) {
                ReportsRow(onClick = onReportsRowClick)
            }
        }
    }
}

/** "N rooms • M devices" for a floor, computed from the real DeviceCatalog. */
private fun floorSubtitle(floorId: String): String {
    val roomCount = DeviceCatalog.roomsForFloor(floorId).size
    val deviceCount = DeviceCatalog.roomsForFloor(floorId).sumOf { DeviceCatalog.devicesForRoom(it.id).size }
    return "$roomCount rooms • $deviceCount devices"
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, value: String, label: String, draw: DrawScope.() -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(30.dp)) { draw() }
        Text(
            text = value,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp),
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )
        Text(
            text = label,
            style = TextStyle(fontSize = 13.sp),
            color = MutedText,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun SectionHeader(title: String, actionText: String?, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = SectionTitleStyle, color = Color.White)
        if (actionText != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onActionClick)
            ) {
                Text(text = actionText, style = TextStyle(fontSize = 14.sp), color = IconCyan)
                Canvas(modifier = Modifier.padding(start = 4.dp).size(14.dp)) { drawChevronRight(IconCyan) }
            }
        }
    }
}

@Composable
private fun SafetyAlertCard(title: String, subtitle: String, timeAgo: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, Danger.copy(alpha = 0.35f), RoundedCornerShape(RadiusCard))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Danger, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(20.dp)) { drawExclamation(Danger) }
        }
        Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
            Text(text = title, style = CardTitleStyle, color = Color.White)
            Text(text = subtitle, style = BodyMutedStyle, color = MutedText, modifier = Modifier.padding(top = 2.dp))
        }
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = timeAgo, style = BodyMutedStyle, color = Danger)
                Canvas(modifier = Modifier.padding(start = 4.dp).size(12.dp)) { drawChevronRight(Danger) }
            }
        }
    }
}

@Composable
private fun FloorCard(modifier: Modifier = Modifier, name: String, subtitle: String, imageRes: Int, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(RadiusMedia))
        )
        Text(
            text = name,
            style = CardTitleStyle,
            color = Color.White,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(text = subtitle, style = BodyMutedStyle, color = MutedText, modifier = Modifier.padding(top = 2.dp))
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusChip))
                .background(Color(0x1A3FC1F0))
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(16.dp)) { drawGrid2x2(IconCyan) }
            Text(
                text = "Tap to open plan",
                style = BodyMutedStyle.copy(fontWeight = FontWeight.Medium),
                color = IconCyan,
                modifier = Modifier.padding(start = 8.dp).weight(1f)
            )
            Canvas(modifier = Modifier.size(14.dp)) { drawChevronRight(IconCyan) }
        }
    }
}

@Composable
private fun ReportsRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusCard))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(RadiusCard))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(28.dp)) { drawBarChart(IconCyan) }
        Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
            Text(text = "Reports", style = CardTitleStyle, color = Color.White)
            Text(
                text = "View usage, activity and safety reports",
                style = BodyMutedStyle,
                color = MutedText,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Canvas(modifier = Modifier.size(16.dp)) { drawChevronRight(MutedText) }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun DashboardScreenPreview() {
    SyncSmartTheme {
        DashboardScreen()
    }
}
