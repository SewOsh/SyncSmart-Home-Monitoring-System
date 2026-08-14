package com.example.syncsmart.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Sync Smart is designed dark-first (dark navy background, indigo/cyan/green
// accents) to match the reference UI. We use the same dark scheme regardless
// of system setting so the brand look stays consistent.
private val SyncSmartDarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    error = Danger,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val SyncSmartLightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    error = Danger
)

@Composable
fun SyncSmartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Force dark to always match the brand reference; flip to `darkTheme`
    // later if you decide to support a real light mode.
    forceDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (forceDark || darkTheme) SyncSmartDarkColorScheme else SyncSmartLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
