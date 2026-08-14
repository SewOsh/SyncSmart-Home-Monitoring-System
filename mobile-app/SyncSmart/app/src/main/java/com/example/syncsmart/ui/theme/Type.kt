package com.example.syncsmart.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using the default system font for now. Swap FontFamily.Default for a bundled
// Inter FontFamily (res/font/) once you add the .ttf files, matching the
// design spec from the UI guide.
val AppTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp)
)

/**
 * Named text-style presets for the recurring type roles across screens.
 * Screens build raw TextStyle(...) inline rather than reading
 * MaterialTheme.typography, so these give every screen's top-bar title,
 * section header, card title, etc. one shared definition instead of each
 * screen picking its own nearby size (22/24/26sp were all used for what is
 * conceptually the same "screen title" role before this was introduced).
 */
val ScreenTitleStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
val SectionTitleStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
val CardTitleStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp)
val BodyStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp)
val BodyMutedStyle = TextStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)
val CaptionStyle = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp)
