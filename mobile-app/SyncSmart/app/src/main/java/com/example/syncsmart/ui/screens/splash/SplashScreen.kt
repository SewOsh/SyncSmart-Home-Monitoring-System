package com.example.syncsmart.ui.screens.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncsmart.R
import com.example.syncsmart.ui.components.Dot
import com.example.syncsmart.ui.components.IconCyan
import com.example.syncsmart.ui.components.drawHouseLogo
import com.example.syncsmart.ui.theme.Accent
import com.example.syncsmart.ui.theme.MutedText
import com.example.syncsmart.ui.theme.SyncSmartTheme
import kotlinx.coroutines.delay

private val TrackDark = Color(0xFF16224A)

/**
 * Splash screen matching the reference design: the real "smart home at
 * night" photo (res/drawable/img.png — already has the five feature badges
 * and dashed connectors baked in) as the background, with the Sync Smart
 * logo mark, title, loading progress bar and pagination dots layered on top.
 */
@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
        ) { value, _ -> progress = value }
        delay(300)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val w = maxWidth
            val h = maxHeight

            // The house + badges + skyline are already part of the photo —
            // only the logo mark and text sit in the empty band below the
            // house and above the skyline.
            val logoCenter = w * 0.50f to h * 0.60f
            val logoRadius = w * 0.20f

            CentralLogo(center = logoCenter, radius = logoRadius)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = h * 0.735f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
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
                    style = TextStyle(fontSize = 14.sp),
                    color = Color(0xFFAEB9D6),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp, start = 24.dp, end = 24.dp)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 34.dp)
                        .fillMaxWidth(0.83f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(TrackDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Brush.horizontalGradient(listOf(IconCyan, Accent)))
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 20.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)
                ) {
                    Dot(active = true)
                    Dot(active = false)
                    Dot(active = false)
                }

                Text(
                    text = "Loading...",
                    style = TextStyle(fontSize = 14.sp),
                    color = MutedText,
                    modifier = Modifier.padding(top = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun CentralLogo(center: Pair<Dp, Dp>, radius: Dp) {
    val glowSize = radius * 2.35f
    Box(
        modifier = Modifier
            .offset(x = center.first - glowSize / 2, y = center.second - glowSize / 2)
            .size(glowSize)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0x333FC1F0), Color.Transparent)))
    )
    Box(
        modifier = Modifier
            .offset(x = center.first - radius, y = center.second - radius)
            .size(radius * 2)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(radius * 0.34f)) {
            drawHouseLogo()
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SplashScreenPreview() {
    SyncSmartTheme {
        SplashScreen()
    }
}
