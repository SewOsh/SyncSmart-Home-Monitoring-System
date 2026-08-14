package com.example.syncsmart.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.example.syncsmart.ui.theme.CaptionStyle
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.RadiusPill

/** Which tab is currently active, shared by every screen that shows the bottom nav. */
internal enum class NavTab { DASHBOARD, FLOORS, DEVICES, REPORTS, SETTINGS }

/** Persistent 5-tab bottom navigation bar, reused by every top-level screen. */
@Composable
internal fun BottomNav(
    selected: NavTab,
    onDashboardClick: () -> Unit,
    onFloorsClick: () -> Unit,
    onDevicesClick: () -> Unit,
    onReportsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // .navigationBarsPadding() keeps this bar clear of the system gesture/3-button
    // nav bar on edge-to-edge devices — without it the bottom row of icons/labels
    // can end up drawn partially underneath the system bar and look cut off.
    Column(modifier = Modifier.fillMaxWidth().background(NavyBg).navigationBarsPadding()) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CardBorder))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem("Dashboard", selected == NavTab.DASHBOARD, onDashboardClick) { drawHouseOutline(it) }
            NavItem("Floors", selected == NavTab.FLOORS, onFloorsClick) { drawGrid2x2(it) }
            NavItem("Devices", selected == NavTab.DEVICES, onDevicesClick) { drawBolt(it) }
            NavItem("Reports", selected == NavTab.REPORTS, onReportsClick) { drawDocument(it) }
            NavItem("Settings", selected == NavTab.SETTINGS, onSettingsClick) { drawGear(it) }
        }
    }
}

@Composable
private fun NavItem(label: String, active: Boolean, onClick: () -> Unit, draw: DrawScope.(Color) -> Unit) {
    val color = if (active) IconCyan else MutedText
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(RadiusPill))
                .background(if (active) IconCyan else Color.Transparent)
        )
        Canvas(modifier = Modifier.padding(top = 6.dp).size(22.dp)) { draw(color) }
        Text(
            text = label,
            style = CaptionStyle.copy(fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal),
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
