package com.example.syncsmart.ui.screens.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.R
import com.example.syncsmart.ui.components.Dot
import com.example.syncsmart.ui.components.Glyph
import com.example.syncsmart.ui.components.IconBadge
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.theme.Accent
import com.example.syncsmart.ui.theme.ButtonBlue
import com.example.syncsmart.ui.theme.CardBg
import com.example.syncsmart.ui.theme.NavyBg
import com.example.syncsmart.ui.theme.SyncSmartTheme

private val RowDivider = Color(0xFF1D2C55)

/**
 * Welcome / onboarding screen shown right after the splash screen finishes:
 * plain dark navy background, a real moon photo (cropped from img_1.png)
 * in the top-right corner, headline, the house photo card (img_2.png) with
 * the five feature badges + dashed connectors overlaid on top of it, three
 * feature rows and a Get Started CTA.
 */
@Composable
fun WelcomeScreen(onGetStarted: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
            // Keeps the headline and the moon photo clear of the status bar
            // on edge-to-edge devices — without it "Welcome to" gets drawn
            // partly underneath the clock/battery icons.
            .statusBarsPadding()
    ) {
        Image(
            painter = painterResource(R.drawable.moon_crop),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 14.dp, end = 4.dp)
                .size(96.dp)
                .clip(CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
        Text(
            text = "Welcome to",
            style = TextStyle(fontWeight = FontWeight.Medium, fontSize = 19.sp),
            color = Color.White,
            modifier = Modifier.padding(top = 28.dp)
        )
        Row(modifier = Modifier.padding(top = 2.dp)) {
            Text(
                text = "Sync ",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp),
                color = Color.White
            )
            Text(
                text = "Smart",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 34.sp),
                color = IconCyan
            )
        }
        Text(
            text = "Smart Home Monitoring & Control System",
            style = TextStyle(fontSize = 15.sp),
            color = Color(0xFFAEB9D6),
            modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
        )

        HouseCard(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp))

        FeatureRow(
            icon = FeatureIcon.SHIELD,
            iconColor = IconCyan,
            title = "Monitor in Real-time",
            description = "Keep an eye on your home from anywhere, anytime."
        )
        HorizontalDivider()
        FeatureRow(
            icon = FeatureIcon.SLIDERS,
            iconColor = IconCyan,
            title = "Control with Ease",
            description = "Control lights, devices, and more with just a tap."
        )
        HorizontalDivider()
        FeatureRow(
            icon = FeatureIcon.HOME,
            iconColor = Accent,
            title = "Smarter Living",
            description = "Automate your home and enjoy a safer, smarter lifestyle."
        )

        GetStartedButton(
            onClick = onGetStarted,
            modifier = Modifier.padding(top = 28.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Dot(active = true)
                Dot(active = false)
                Dot(active = false)
            }
        }
        }
    }
}

@Composable
private fun HouseCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(785f / 708f)
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .border(1.dp, IconCyan.copy(alpha = 0.30f), RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(R.drawable.img_2),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = maxWidth
            val h = maxHeight

            val wifiPos = w * 0.185f to h * 0.226f
            val bulbPos = w * 0.497f to h * 0.113f
            val cameraPos = w * 0.805f to h * 0.226f
            val lockPos = w * 0.087f to h * 0.523f
            val thermoPos = w * 0.902f to h * 0.523f
            val houseCenterX = w * 0.5f

            Canvas(modifier = Modifier.fillMaxSize()) {
                val bendY = size.height * 0.40f
                val dash = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                val lineColor = Color(0x8CA9D8FF)
                listOf(wifiPos, bulbPos, cameraPos, lockPos, thermoPos).forEach { (bx, by) ->
                    val start = Offset(bx.toPx(), by.toPx())
                    val mid = Offset(start.x, bendY)
                    val end = Offset(houseCenterX.toPx(), bendY)
                    drawLine(lineColor, start, mid, strokeWidth = 2f, pathEffect = dash)
                    drawLine(lineColor, mid, end, strokeWidth = 2f, pathEffect = dash)
                }
            }

            IconBadge(Glyph.WIFI, wifiPos)
            IconBadge(Glyph.BULB, bulbPos)
            IconBadge(Glyph.CAMERA, cameraPos)
            IconBadge(Glyph.LOCK, lockPos)
            IconBadge(Glyph.THERMO, thermoPos)
        }
    }
}

@Composable
private fun HorizontalDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .height(1.dp)
            .background(RowDivider)
    )
}

private enum class FeatureIcon { SHIELD, SLIDERS, HOME }

@Composable
private fun FeatureRow(icon: FeatureIcon, iconColor: Color, title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x1A3FC1F0))
                .border(1.dp, iconColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                when (icon) {
                    FeatureIcon.SHIELD -> drawShieldCheck(iconColor)
                    FeatureIcon.SLIDERS -> drawSliders(iconColor)
                    FeatureIcon.HOME -> drawHomeOutline(iconColor)
                }
            }
        }
        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
            Text(text = title, style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp), color = Color.White)
            Text(
                text = description,
                style = TextStyle(fontSize = 14.sp),
                color = Color(0xFF9AA6C7),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun GetStartedButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(ButtonBlue, Accent)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Get started",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            color = Color.White
        )
        Box(modifier = Modifier.fillMaxWidth().padding(end = 22.dp), contentAlignment = Alignment.CenterEnd) {
            Canvas(modifier = Modifier.size(22.dp)) {
                drawArrow(Color.White)
            }
        }
    }
}

private fun DrawScope.drawArrow(color: Color) {
    val w = size.width
    val h = size.height
    drawLine(color, Offset(0f, h / 2), Offset(w * 0.75f, h / 2), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
    val head = Path().apply {
        moveTo(w * 0.45f, h * 0.15f)
        lineTo(w * 0.9f, h * 0.5f)
        lineTo(w * 0.45f, h * 0.85f)
    }
    drawPath(head, color = color, style = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

private fun DrawScope.drawShieldCheck(color: Color) {
    val w = size.width
    val h = size.height
    val shield = Path().apply {
        moveTo(w * 0.5f, h * 0.02f)
        lineTo(w * 0.92f, h * 0.18f)
        lineTo(w * 0.92f, h * 0.55f)
        quadraticBezierTo(w * 0.92f, h * 0.85f, w * 0.5f, h * 0.98f)
        quadraticBezierTo(w * 0.08f, h * 0.85f, w * 0.08f, h * 0.55f)
        lineTo(w * 0.08f, h * 0.18f)
        close()
    }
    drawPath(shield, color = color, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
    val check = Path().apply {
        moveTo(w * 0.32f, h * 0.50f)
        lineTo(w * 0.45f, h * 0.64f)
        lineTo(w * 0.70f, h * 0.36f)
    }
    drawPath(check, color = color, style = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

private fun DrawScope.drawSliders(color: Color) {
    val w = size.width
    val h = size.height
    val stroke = w * 0.08f
    val columns = listOf(0.22f, 0.5f, 0.78f)
    val knobY = listOf(0.35f, 0.60f, 0.45f)
    columns.forEachIndexed { i, fx ->
        drawLine(color, Offset(w * fx, h * 0.05f), Offset(w * fx, h * 0.95f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(color = color, radius = w * 0.09f, center = Offset(w * fx, h * knobY[i]))
    }
}

private fun DrawScope.drawHomeOutline(color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
    val roof = Path().apply {
        moveTo(w * 0.08f, h * 0.50f)
        lineTo(w * 0.5f, h * 0.08f)
        lineTo(w * 0.92f, h * 0.50f)
    }
    drawPath(roof, color = color, style = stroke)
    drawLine(color, Offset(w * 0.20f, h * 0.48f), Offset(w * 0.20f, h * 0.92f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.80f, h * 0.48f), Offset(w * 0.80f, h * 0.92f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.20f, h * 0.92f), Offset(w * 0.80f, h * 0.92f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.42f, h * 0.60f),
        size = Size(w * 0.16f, h * 0.32f),
        cornerRadius = CornerRadius(w * 0.03f),
        style = Stroke(width = w * 0.07f)
    )
}

@Preview(showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun WelcomeScreenPreview() {
    SyncSmartTheme {
        WelcomeScreen()
    }
}
