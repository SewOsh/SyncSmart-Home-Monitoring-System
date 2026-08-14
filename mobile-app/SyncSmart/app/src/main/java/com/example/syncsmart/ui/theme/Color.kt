package com.example.syncsmart.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF4F46E5)
val Secondary = Color(0xFF06B6D4)
val Accent = Color(0xFF22C55E)
val Warning = Color(0xFFF59E0B)
val Danger = Color(0xFFEF4444)
// Updated to match the navy/card/muted palette every screen actually renders
// (each screen originally declared its own identical private copies of these
// hex values instead of using this shared theme — these were out of sync).
val BackgroundDark = Color(0xFF060C22)
val SurfaceDark = Color(0xFF0B1533)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8B96B8)

// Convenience aliases matching the identifier names screens already use
// locally, so replacing a screen's private `val NavyBg = Color(0x060C22)`
// with `import com.example.syncsmart.ui.theme.NavyBg` is a pure swap with no
// call-site renaming required.
val NavyBg = BackgroundDark
val CardBg = SurfaceDark
val MutedText = TextSecondary

/** Primary gradient/action blue used on CTA buttons, active toggles and links. */
val ButtonBlue = Color(0xFF2E6BFF)

/** Recessed dark surface used for toggle tracks, inline avatar chips, social buttons. */
val SurfaceInset = Color(0xFF15213D)
