package com.example.syncsmart.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Shared corner-radius and spacing scale so every screen's cards, chips,
 * panels and buttons round the same amount instead of each screen picking
 * its own nearby value (8/10/12/14/16/18/20/24dp were all in use for what
 * was conceptually the same "card" before this was introduced).
 */

/** Small tags/pills — section labels, TYPE badge, floor-switch badge. */
val RadiusChip = 10.dp

/** Segmented toggle buttons, zoom controls, small square icon buttons. */
val RadiusButton = 14.dp

/** Standard content cards — stat cards, alert cards, usage rows, sub-panels. */
val RadiusCard = 20.dp

/** Large hero panels — the floor-plan blueprint, the welcome house card. */
val RadiusPanel = 24.dp

/** Embedded photo/thumbnail corners (smaller than the card that contains them). */
val RadiusMedia = 16.dp

/** Fully-rounded elements — toggles, progress bars, pill CTA buttons. */
val RadiusPill = 50.dp

val SpacingXs = 4.dp
val SpacingSm = 8.dp
val SpacingMd = 12.dp
val SpacingLg = 16.dp
val SpacingXl = 20.dp
val SpacingXxl = 24.dp
