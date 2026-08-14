package com.example.syncsmart.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate

/**
 * Hand-drawn Canvas glyphs for the Dashboard screen (top bar, stat cards,
 * bottom nav). Same rationale as SmartHomeGlyphs.kt — no icon-library
 * dependency to verify, everything drawn with plain DrawScope primitives.
 */

internal fun DrawScope.drawBell(color: Color) {
    val w = size.width
    val h = size.height
    val body = Path().apply {
        moveTo(w * 0.5f, h * 0.06f)
        cubicTo(w * 0.24f, h * 0.06f, w * 0.20f, h * 0.30f, w * 0.20f, h * 0.48f)
        lineTo(w * 0.20f, h * 0.62f)
        lineTo(w * 0.08f, h * 0.78f)
        lineTo(w * 0.92f, h * 0.78f)
        lineTo(w * 0.80f, h * 0.62f)
        lineTo(w * 0.80f, h * 0.48f)
        cubicTo(w * 0.80f, h * 0.30f, w * 0.76f, h * 0.06f, w * 0.5f, h * 0.06f)
        close()
    }
    drawPath(body, color = color, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawArc(
        color = color,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        style = Stroke(width = w * 0.08f, cap = StrokeCap.Round),
        topLeft = Offset(w * 0.32f, h * 0.80f),
        size = Size(w * 0.36f, h * 0.28f)
    )
}

internal fun DrawScope.drawHouseOutline(color: Color) {
    val w = size.width
    val h = size.height
    val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val roof = Path().apply {
        moveTo(w * 0.06f, h * 0.52f)
        lineTo(w * 0.5f, h * 0.08f)
        lineTo(w * 0.94f, h * 0.52f)
    }
    drawPath(roof, color = color, style = stroke)
    drawLine(color, Offset(w * 0.20f, h * 0.50f), Offset(w * 0.20f, h * 0.92f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.80f, h * 0.50f), Offset(w * 0.80f, h * 0.92f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.20f, h * 0.92f), Offset(w * 0.80f, h * 0.92f), strokeWidth = w * 0.09f, cap = StrokeCap.Round)
}

internal fun DrawScope.drawChevronRight(color: Color) {
    val w = size.width
    val h = size.height
    val chevron = Path().apply {
        moveTo(w * 0.30f, h * 0.15f)
        lineTo(w * 0.75f, h * 0.5f)
        lineTo(w * 0.30f, h * 0.85f)
    }
    drawPath(chevron, color = color, style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

internal fun DrawScope.drawChevronDown(color: Color) {
    val w = size.width
    val h = size.height
    val chevron = Path().apply {
        moveTo(w * 0.15f, h * 0.30f)
        lineTo(w * 0.5f, h * 0.72f)
        lineTo(w * 0.85f, h * 0.30f)
    }
    drawPath(chevron, color = color, style = Stroke(width = w * 0.13f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

internal fun DrawScope.drawChevronLeft(color: Color) {
    val w = size.width
    val h = size.height
    val chevron = Path().apply {
        moveTo(w * 0.70f, h * 0.15f)
        lineTo(w * 0.25f, h * 0.5f)
        lineTo(w * 0.70f, h * 0.85f)
    }
    drawPath(chevron, color = color, style = Stroke(width = w * 0.14f, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

internal fun DrawScope.drawInfo(color: Color) {
    val w = size.width
    val h = size.height
    drawCircle(color = color, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.09f))
    drawCircle(color = color, radius = w * 0.055f, center = Offset(w * 0.5f, h * 0.30f))
    drawLine(color, Offset(w * 0.5f, h * 0.46f), Offset(w * 0.5f, h * 0.74f), strokeWidth = w * 0.11f, cap = StrokeCap.Round)
}

internal fun DrawScope.drawPlus(color: Color) {
    val w = size.width
    val h = size.height
    drawLine(color, Offset(w * 0.5f, h * 0.14f), Offset(w * 0.5f, h * 0.86f), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.14f, h * 0.5f), Offset(w * 0.86f, h * 0.5f), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
}

internal fun DrawScope.drawMinus(color: Color) {
    val w = size.width
    val h = size.height
    drawLine(color, Offset(w * 0.14f, h * 0.5f), Offset(w * 0.86f, h * 0.5f), strokeWidth = w * 0.12f, cap = StrokeCap.Round)
}

internal fun DrawScope.drawOutlet(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.14f, h * 0.14f),
        size = Size(w * 0.72f, h * 0.72f),
        cornerRadius = CornerRadius(w * 0.14f),
        style = Stroke(width = w * 0.08f)
    )
    drawLine(color, Offset(w * 0.38f, h * 0.36f), Offset(w * 0.38f, h * 0.56f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.62f, h * 0.36f), Offset(w * 0.62f, h * 0.56f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    drawCircle(color = color, radius = w * 0.05f, center = Offset(w * 0.5f, h * 0.68f))
}

internal fun DrawScope.drawBlinds(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.16f, h * 0.08f),
        size = Size(w * 0.68f, h * 0.84f),
        cornerRadius = CornerRadius(w * 0.10f),
        style = Stroke(width = w * 0.07f)
    )
    listOf(0.28f, 0.44f, 0.60f, 0.76f).forEach { fy ->
        drawLine(color, Offset(w * 0.20f, h * fy), Offset(w * 0.80f, h * fy), strokeWidth = w * 0.055f, cap = StrokeCap.Round)
    }
}

internal fun DrawScope.drawExclamation(color: Color) {
    val w = size.width
    val h = size.height
    drawCircle(color = color, radius = w * 0.42f, center = Offset(w * 0.5f, h * 0.5f), style = Stroke(width = w * 0.09f))
    drawLine(color, Offset(w * 0.5f, h * 0.28f), Offset(w * 0.5f, h * 0.56f), strokeWidth = w * 0.11f, cap = StrokeCap.Round)
    drawCircle(color = color, radius = w * 0.055f, center = Offset(w * 0.5f, h * 0.72f))
}

internal fun DrawScope.drawGrid2x2(color: Color) {
    val w = size.width
    val h = size.height
    val gap = w * 0.14f
    val cell = (w - gap) / 2f
    val r = CornerRadius(cell * 0.18f)
    drawRoundRect(color, topLeft = Offset(0f, 0f), size = Size(cell, cell), cornerRadius = r)
    drawRoundRect(color, topLeft = Offset(cell + gap, 0f), size = Size(cell, cell), cornerRadius = r)
    drawRoundRect(color, topLeft = Offset(0f, cell + gap), size = Size(cell, cell), cornerRadius = r)
    drawRoundRect(color, topLeft = Offset(cell + gap, cell + gap), size = Size(cell, cell), cornerRadius = r)
}

internal fun DrawScope.drawBarChart(color: Color) {
    val w = size.width
    val h = size.height
    val bars = listOf(0.35f to 0.5f, 0.55f to 0.7f, 0.75f to 0.9f, 0.95f to 0.65f)
    val barW = w * 0.14f
    bars.forEach { (fx, fh) ->
        drawRoundRect(
            color = color,
            topLeft = Offset(w * fx - barW, h * (1f - fh)),
            size = Size(barW, h * fh),
            cornerRadius = CornerRadius(barW * 0.3f)
        )
    }
}

internal fun DrawScope.drawBolt(color: Color) {
    val w = size.width
    val h = size.height
    val bolt = Path().apply {
        moveTo(w * 0.56f, h * 0.04f)
        lineTo(w * 0.14f, h * 0.58f)
        lineTo(w * 0.46f, h * 0.58f)
        lineTo(w * 0.40f, h * 0.96f)
        lineTo(w * 0.88f, h * 0.38f)
        lineTo(w * 0.54f, h * 0.38f)
        close()
    }
    drawPath(bolt, color = color)
}

internal fun DrawScope.drawDocument(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.14f, h * 0.04f),
        size = Size(w * 0.72f, h * 0.92f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = Stroke(width = w * 0.08f)
    )
    listOf(0.32f, 0.50f, 0.68f).forEach { fy ->
        drawLine(color, Offset(w * 0.28f, h * fy), Offset(w * 0.72f, h * fy), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
    }
}

internal fun DrawScope.drawGear(color: Color) {
    val w = size.width
    val h = size.height
    val center = Offset(w / 2f, h / 2f)
    val toothW = w * 0.16f
    val toothH = h * 0.18f
    for (i in 0 until 8) {
        rotate(degrees = i * 45f, pivot = center) {
            drawRoundRect(
                color = color,
                topLeft = Offset(center.x - toothW / 2f, 0f),
                size = Size(toothW, toothH),
                cornerRadius = CornerRadius(toothW * 0.3f)
            )
        }
    }
    drawCircle(color = color, radius = w * 0.28f, center = center, style = Stroke(width = w * 0.09f))
    drawCircle(color = color, radius = w * 0.09f, center = center)
}

internal fun DrawScope.drawPerson(color: Color) {
    val w = size.width
    val h = size.height
    drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.32f), style = Stroke(width = w * 0.08f))
    val shoulders = Path().apply {
        moveTo(w * 0.16f, h * 0.88f)
        cubicTo(w * 0.16f, h * 0.62f, w * 0.84f, h * 0.62f, w * 0.84f, h * 0.88f)
    }
    drawPath(shoulders, color = color, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round))
}

internal fun DrawScope.drawHamburger(color: Color) {
    val w = size.width
    val h = size.height
    listOf(0.22f, 0.5f, 0.78f).forEach { fy ->
        drawLine(color, Offset(w * 0.12f, h * fy), Offset(w * 0.88f, h * fy), strokeWidth = w * 0.10f, cap = StrokeCap.Round)
    }
}

/** Flame glyph — used for the "Iron Socket (Safety Device)" overheat marker. */
internal fun DrawScope.drawFlame(color: Color) {
    val w = size.width
    val h = size.height
    val flame = Path().apply {
        moveTo(w * 0.5f, h * 0.04f)
        cubicTo(w * 0.80f, h * 0.28f, w * 0.88f, h * 0.56f, w * 0.66f, h * 0.74f)
        cubicTo(w * 0.72f, h * 0.58f, w * 0.58f, h * 0.50f, w * 0.55f, h * 0.60f)
        cubicTo(w * 0.50f, h * 0.74f, w * 0.60f, h * 0.84f, w * 0.50f, h * 0.97f)
        cubicTo(w * 0.20f, h * 0.90f, w * 0.12f, h * 0.64f, w * 0.27f, h * 0.44f)
        cubicTo(w * 0.30f, h * 0.58f, w * 0.38f, h * 0.60f, w * 0.38f, h * 0.48f)
        cubicTo(w * 0.38f, h * 0.28f, w * 0.30f, h * 0.18f, w * 0.5f, h * 0.04f)
        close()
    }
    drawPath(flame, color = color)
}

/** Snowflake glyph — used for the Air Conditioner marker. */
internal fun DrawScope.drawSnowflake(color: Color) {
    val w = size.width
    val h = size.height
    val center = Offset(w / 2f, h / 2f)
    val r = w * 0.42f
    for (i in 0 until 3) {
        rotate(degrees = i * 60f, pivot = center) {
            drawLine(color, Offset(center.x, center.y - r), Offset(center.x, center.y + r), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
        }
    }
    for (i in 0 until 6) {
        rotate(degrees = i * 60f, pivot = center) {
            val tip = Offset(center.x, center.y - r)
            drawLine(color, tip, Offset(tip.x - w * 0.12f, tip.y + h * 0.16f), strokeWidth = w * 0.055f, cap = StrokeCap.Round)
            drawLine(color, tip, Offset(tip.x + w * 0.12f, tip.y + h * 0.16f), strokeWidth = w * 0.055f, cap = StrokeCap.Round)
        }
    }
}

/** Vertical three-dot overflow-menu glyph, used by the Device Control top bar. */
internal fun DrawScope.drawThreeDotsVertical(color: Color) {
    val w = size.width
    val h = size.height
    listOf(0.18f, 0.5f, 0.82f).forEach { fy ->
        drawCircle(color = color, radius = w * 0.09f, center = Offset(w * 0.5f, h * fy))
    }
}

/** Solid filled bulb — the large "Smart Light" hero icon (distinct from the outline drawBulb glyph). */
internal fun DrawScope.drawBulbFilled(color: Color) {
    val w = size.width
    val h = size.height
    drawCircle(color = color, radius = w * 0.34f, center = Offset(w / 2f, h * 0.38f))
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.36f, h * 0.62f),
        size = Size(w * 0.28f, h * 0.16f),
        cornerRadius = CornerRadius(w * 0.04f)
    )
    drawLine(color, Offset(w * 0.40f, h * 0.86f), Offset(w * 0.60f, h * 0.86f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
}

/** Hanging dome/pendant light — used for "Main Light". */
internal fun DrawScope.drawDome(color: Color) {
    val w = size.width
    val h = size.height
    drawLine(color, Offset(w * 0.5f, h * 0.04f), Offset(w * 0.5f, h * 0.30f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
    val dome = Path().apply {
        moveTo(w * 0.14f, h * 0.64f)
        cubicTo(w * 0.14f, h * 0.34f, w * 0.86f, h * 0.34f, w * 0.86f, h * 0.64f)
    }
    drawPath(dome, color = color, style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawLine(color, Offset(w * 0.10f, h * 0.64f), Offset(w * 0.90f, h * 0.64f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
}

/** Downward spotlight beam — used for "Spot Lights". */
internal fun DrawScope.drawSpotBeam(color: Color) {
    val w = size.width
    val h = size.height
    drawArc(
        color = color,
        startAngle = 200f,
        sweepAngle = 140f,
        useCenter = false,
        style = Stroke(width = w * 0.08f, cap = StrokeCap.Round),
        topLeft = Offset(w * 0.10f, h * 0.04f),
        size = Size(w * 0.80f, h * 0.46f)
    )
    drawCircle(color = color, radius = w * 0.07f, center = Offset(w * 0.5f, h * 0.28f))
    listOf(0.30f, 0.5f, 0.70f).forEach { fx ->
        drawLine(color, Offset(w * fx, h * 0.60f), Offset(w * fx, h * 0.92f), strokeWidth = w * 0.06f, cap = StrokeCap.Round)
    }
}

/** LED strip bar with segment ticks — used for "LED Strip". */
internal fun DrawScope.drawStripLight(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.05f, h * 0.38f),
        size = Size(w * 0.90f, h * 0.24f),
        cornerRadius = CornerRadius(w * 0.06f),
        style = Stroke(width = w * 0.07f)
    )
    listOf(0.20f, 0.36f, 0.52f, 0.68f, 0.84f).forEach { fx ->
        drawLine(color, Offset(w * fx, h * 0.42f), Offset(w * fx, h * 0.58f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
    }
}

/** Calendar-with-clock-badge glyph — used for the "Schedule" row. */
internal fun DrawScope.drawCalendarClock(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.04f, h * 0.18f),
        size = Size(w * 0.62f, h * 0.66f),
        cornerRadius = CornerRadius(w * 0.08f),
        style = Stroke(width = w * 0.07f)
    )
    drawLine(color, Offset(w * 0.20f, h * 0.06f), Offset(w * 0.20f, h * 0.28f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.50f, h * 0.06f), Offset(w * 0.50f, h * 0.28f), strokeWidth = w * 0.07f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.04f, h * 0.40f), Offset(w * 0.66f, h * 0.40f), strokeWidth = w * 0.05f)
    drawCircle(color = color, radius = w * 0.24f, center = Offset(w * 0.78f, h * 0.78f), style = Stroke(width = w * 0.06f))
    drawLine(color, Offset(w * 0.78f, h * 0.78f), Offset(w * 0.78f, h * 0.66f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.78f, h * 0.78f), Offset(w * 0.87f, h * 0.80f), strokeWidth = w * 0.045f, cap = StrokeCap.Round)
}

/** Filled right-pointing play triangle, used over the camera preview thumbnail. */
internal fun DrawScope.drawPlayTriangle(color: Color) {
    val w = size.width
    val h = size.height
    val path = Path().apply {
        moveTo(w * 0.28f, h * 0.16f)
        lineTo(w * 0.28f, h * 0.84f)
        lineTo(w * 0.84f, h * 0.5f)
        close()
    }
    drawPath(path, color = color)
}

/** Four-corner expand/fullscreen brackets glyph. */
internal fun DrawScope.drawExpandArrows(color: Color) {
    val w = size.width
    val h = size.height
    val len = w * 0.26f
    val stroke = Stroke(width = w * 0.10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(Path().apply {
        moveTo(w * 0.06f, h * 0.06f + len); lineTo(w * 0.06f, h * 0.06f); lineTo(w * 0.06f + len, h * 0.06f)
    }, color = color, style = stroke)
    drawPath(Path().apply {
        moveTo(w * 0.94f - len, h * 0.06f); lineTo(w * 0.94f, h * 0.06f); lineTo(w * 0.94f, h * 0.06f + len)
    }, color = color, style = stroke)
    drawPath(Path().apply {
        moveTo(w * 0.06f, h * 0.94f - len); lineTo(w * 0.06f, h * 0.94f); lineTo(w * 0.06f + len, h * 0.94f)
    }, color = color, style = stroke)
    drawPath(Path().apply {
        moveTo(w * 0.94f - len, h * 0.94f); lineTo(w * 0.94f, h * 0.94f); lineTo(w * 0.94f, h * 0.94f - len)
    }, color = color, style = stroke)
}

/** Plain calendar glyph (no clock badge) — used on the Reports date-range cards. */
internal fun DrawScope.drawCalendar(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.08f, h * 0.18f),
        size = Size(w * 0.84f, h * 0.74f),
        cornerRadius = CornerRadius(w * 0.10f),
        style = Stroke(width = w * 0.08f)
    )
    drawLine(color, Offset(w * 0.26f, h * 0.06f), Offset(w * 0.26f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.74f, h * 0.06f), Offset(w * 0.74f, h * 0.28f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
    drawLine(color, Offset(w * 0.08f, h * 0.42f), Offset(w * 0.92f, h * 0.42f), strokeWidth = w * 0.05f)
    listOf(0.30f to 0.58f, 0.5f to 0.58f, 0.70f to 0.58f, 0.30f to 0.76f, 0.5f to 0.76f).forEach { (fx, fy) ->
        drawCircle(color = color, radius = w * 0.045f, center = Offset(w * fx, h * fy))
    }
}

/** Three-blade fan glyph — used for the "Dining Room Fan" usage card. */
internal fun DrawScope.drawFan(color: Color) {
    val w = size.width
    val h = size.height
    val center = Offset(w / 2f, h / 2f)
    for (i in 0 until 3) {
        rotate(degrees = i * 120f, pivot = center) {
            val blade = Path().apply {
                moveTo(center.x, center.y)
                quadraticBezierTo(center.x + w * 0.36f, center.y - h * 0.10f, center.x + w * 0.06f, center.y - h * 0.40f)
                quadraticBezierTo(center.x - w * 0.10f, center.y - h * 0.20f, center.x, center.y)
                close()
            }
            drawPath(blade, color = color)
        }
    }
    drawCircle(color = color, radius = w * 0.10f, center = center)
}

/** Floppy-disk save glyph, used on the "Save changes" button. */
internal fun DrawScope.drawSave(color: Color) {
    val w = size.width
    val h = size.height
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.10f, h * 0.08f),
        size = Size(w * 0.80f, h * 0.84f),
        cornerRadius = CornerRadius(w * 0.10f),
        style = Stroke(width = w * 0.08f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.28f, h * 0.08f),
        size = Size(w * 0.44f, h * 0.28f),
        cornerRadius = CornerRadius(w * 0.03f),
        style = Stroke(width = w * 0.07f)
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(w * 0.24f, h * 0.52f),
        size = Size(w * 0.52f, h * 0.36f),
        cornerRadius = CornerRadius(w * 0.04f),
        style = Stroke(width = w * 0.07f)
    )
}
