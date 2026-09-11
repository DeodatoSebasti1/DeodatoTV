package tv.own.owntv.ui.theme

import androidx.compose.ui.unit.dp

/** Shared spacing / sizing tokens for the 4-layer TV shell. */
object Dimens {
    val ScreenPaddingH = 34.dp
    val ScreenPaddingV = 24.dp

    // Layer 1 — MD3 navigation panel. Expands to a drawer (labels) when focused,
    // collapses to an icon rail when focus moves into a submenu.
    val SidebarWidthExpanded = 276.dp
    val SidebarWidthCollapsed = 90.dp
    val TopBarHeight = 48.dp
    /** The strip while the audio capsule is focused/activated — the capsule's tall rendering plus its inset. */
    val TopBarAudioExpandedHeight = 70.dp
    /** Normal chrome has no tall audio controls, so it can sit closer to the content panel. */
    val TopBarCompactHeight = 40.dp

    // Layer 2 — category rail (expands to show full names when it holds focus)
    val RailWidth = 92.dp
    val RailWidthExpanded = 325.dp // expanded (focused overlay) width — fits long category names
    val RailWidthFixed = 276.dp    // Phase 2: fixed full-label category column (names wrap to 2 lines)
    val RailPillSize = 56.dp
    val ChannelListWidth = 460.dp  // Phase 2: fixed channel-list column (Live); preview/detail fills the rest.
    // Widened from 400dp so each row can also show the current programme (EPG) on a second line — the
    // preview pane (Modifier.weight(1f)) narrows in step, so no other layout value needs to change.

    // MD3 settings tonal icon tile
    val IconTileSize = 42.dp
    val IconTileCorner = 12.dp

    val GapTiny = 4.dp
    val GapSmall = 8.dp
    val GapMedium = 16.dp
    val GapLarge = 24.dp

    // Poster tiles (PosterCard) — values match the shipped look exactly; centralized for tuning.
    val PosterCardCorner = 18.dp
    val PosterArtCorner = 14.dp
    val PosterPadding = 7.dp
    val PosterProgressHeight = 5.dp

    // M3 expressive shape scale (larger, rounder than the defaults).
    val CornerSmall = 14.dp
    val CornerMedium = 20.dp
    val CornerLarge = 28.dp
    val CardCorner = 22.dp

    val FocusBorderWidth = 2.5.dp

    val HomeRowPaddingH = 20.dp

    // Hero carousel
    val HeroBaseWidth = 180.dp
    val HeroMetaHeight = 84.dp
    val HeroGap = 14.dp
    val HeroCardCorner = 22.dp
    val HeroPosterCorner = 16.dp
    val HeroMaxCardHeight = 354.dp
    val HeroMinCardHeight = 200.dp
    val HeroOverlayMaxWidth = 400.dp
    val HeroProgressHeight = 3.dp
}
