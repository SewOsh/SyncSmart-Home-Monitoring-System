package com.example.syncsmart.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.syncsmart.ui.theme.Accent

/**
 * Shared visual language for the Sync Smart onboarding screens (Splash,
 * Welcome): the cyan icon badges, their hand-drawn glyphs, and the house/wifi
 * logo mark. Centralized here so Splash and Welcome stay pixel-consistent
 * instead of each screen re-implementing its own version.
 */
internal val IconCyan = Color(0xFF3FC1F0)
internal val LogoBlue = Color(0xFF1D4ED8)

/** Standard faint card border used on every bordered card/panel across screens. */
internal val CardBorder = IconCyan.copy(alpha = 0.25f)

internal enum class Glyph { WIFI, BULB, CAMERA, LOCK, THERMO }

@Composable
internal fun IconBadge(glyph: Glyph, centerFraction: Pair<Dp, Dp>, size: Dp = 58.dp) {
    Box(
        modifier = Modifier
            .offset(x = centerFraction.first - size / 2, y = centerFraction.second - size / 2)
            .size(size)
            .clip(CircleShape)
            .background(Color(0x33113058))
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color(0x1A3FC1F0))
        )
        Canvas(modifier = Modifier.fillMaxSize().padding(size * 0.24f)) {
            when (glyph) {
                Glyph.WIFI -> drawWifi(IconCyan)
                Glyph.BULB -> drawBulb(IconCyan)
                Glyph.CAMERA -> drawCamera(IconCyan)
                Glyph.LOCK -> drawLock(IconCyan)
                Glyph.THERMO -> drawThermo(IconCyan)
            }
        }
    }
}

/** The circular house/wifi/leaf logo mark, reused wherever the brand mark
 * appears inline (Login banner, etc). Splash's CentralLogo has its own copy
 * since it also needs absolute positioning + an outer glow halo. */
@Composable
internal fun LogoMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .border(2.dp, IconCyan.copy(alpha = 0.55f), CircleShape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            drawHouseLogo()
        }
    }
}

@Composable
internal fun Dot(active: Boolean, activeColor: Color = IconCyan) {
    Box(
        modifier = Modifier
            .size(if (active) 11.dp else 9.dp)
            .clip(CircleShape)
            .background(if (active) activeColor else Color(0xFF223258))
    )
}

internal fun DrawScope.drawWifi(color: Color) {
    val w = size.width
    val center = Offset(w / 2f, size.height * 0.82f)
    val radii = listOf(0.20f, 0.40f, 0.60f).map { it * w }
    val strokeW = w * 0.10f
    radii.forEach { r ->
        drawArc(
            color = color,
            startAngle = 210f,
            sweepAngle = 120f,
            useCenter = false,
            style = Stroke(width = strokeW, cap = StrokeCap.Round),
            topLeft = Offset(center.x - r, center.y - r),
            size = Size(r * 2, r * 2)
        )
    }
    drawCircle(color = color, radius = w * 0.07f, center = center)
}

internal fun DrawScope.drawBulb(color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
    drawCircle(
        color = color,
        radius = w * 0.36f,
        center = Offset(w / 2f, h * 0.36f),
        style = stroke
    )
    drawLine(color, Offset(w * 0.40f, h * 0.68f), Offset(w * 0.60f, h * 0.68f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.44f, h * 0.84f), Offset(w * 0.56f, h * 0.84f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
}

internal fun DrawScope.drawCamera(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.05f, h * 0.30f),
        size = Size(w * 0.70f, h * 0.42f),
        cornerRadius = CornerRadius(w * 0.10f),
        style = Stroke(width = w * 0.09f)
    )
    drawCircle(color = color, radius = w * 0.16f, center = Offset(w * 0.76f, h * 0.51f))
    val mount = Path().apply {
        moveTo(w * 0.15f, h * 0.30f)
        lineTo(w * 0.30f, h * 0.10f)
        lineTo(w * 0.50f, h * 0.10f)
        lineTo(w * 0.45f, h * 0.30f)
        close()
    }
    drawPath(mount, color = color)
}

internal fun DrawScope.drawLock(color: Color) {
    val w = size.width
    val h = size.height
    drawArc(
        color = color,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        style = Stroke(width = w * 0.10f, cap = StrokeCap.Round),
        topLeft = Offset(w * 0.24f, h * 0.02f),
        size = Size(w * 0.52f, w * 0.52f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.14f, h * 0.42f),
        size = Size(w * 0.72f, h * 0.52f),
        cornerRadius = CornerRadius(w * 0.12f)
    )
}

internal fun DrawScope.drawThermo(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.38f, h * 0.04f),
        size = Size(w * 0.24f, h * 0.62f),
        cornerRadius = CornerRadius(w * 0.12f),
        style = Stroke(width = w * 0.09f)
    )
    drawCircle(color = color, radius = w * 0.20f, center = Offset(w * 0.5f, h * 0.82f))
    drawLine(color, Offset(w * 0.62f, h * 0.20f), Offset(w * 0.72f, h * 0.20f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.62f, h * 0.34f), Offset(w * 0.72f, h * 0.34f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
}

/** Stylized house-with-wifi-signal over a green leaf/hand shape (the app logo mark). */
internal fun DrawScope.drawHouseLogo() {
    val w = size.width
    val h = size.height
    val roofStroke = Stroke(width = w * 0.075f, cap = StrokeCap.Round, join = StrokeJoin.Round)

    val roof = Path().apply {
        moveTo(w * 0.10f, h * 0.42f)
        lineTo(w * 0.44f, h * 0.06f)
        lineTo(w * 0.70f, h * 0.30f)
    }
    drawPath(roof, color = LogoBlue, style = roofStroke)

    val poleTop = Offset(w * 0.70f, h * 0.06f)
    drawLine(LogoBlue, Offset(w * 0.70f, h * 0.30f), poleTop, strokeWidth = w * 0.06f, cap = StrokeCap.Round)
    listOf(0.10f, 0.18f).forEach { r ->
        drawArc(
            color = LogoBlue,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            style = Stroke(width = w * 0.045f, cap = StrokeCap.Round),
            topLeft = Offset(poleTop.x - r * w, poleTop.y - r * w),
            size = Size(r * w * 2, r * w * 2)
        )
    }

    drawLine(LogoBlue, Offset(w * 0.10f, h * 0.42f), Offset(w * 0.10f, h * 0.62f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
    drawLine(LogoBlue, Offset(w * 0.58f, h * 0.44f), Offset(w * 0.58f, h * 0.62f), strokeWidth = w * 0.075f, cap = StrokeCap.Round)
    drawRoundRect(
        color = LogoBlue,
        topLeft = Offset(w * 0.20f, h * 0.48f),
        size = Size(w * 0.13f, h * 0.13f),
        cornerRadius = CornerRadius(w * 0.02f)
    )
    drawRoundRect(
        color = LogoBlue,
        topLeft = Offset(w * 0.36f, h * 0.48f),
        size = Size(w * 0.13f, h * 0.13f),
        cornerRadius = CornerRadius(w * 0.02f)
    )

    val leaf = Path().apply {
        moveTo(w * 0.06f, h * 0.78f)
        quadraticBezierTo(w * 0.40f, h * 0.60f, w * 0.94f, h * 0.80f)
        quadraticBezierTo(w * 0.55f, h * 1.02f, w * 0.06f, h * 0.78f)
        close()
    }
    drawPath(
        path = leaf,
        brush = Brush.linearGradient(
            colors = listOf(Accent, Color(0xFF06B6D4)),
            start = Offset(0f, h * 0.6f),
            end = Offset(w, h)
        )
    )
}
