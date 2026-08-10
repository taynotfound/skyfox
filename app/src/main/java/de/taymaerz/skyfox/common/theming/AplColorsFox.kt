package de.taymaerz.skyfox.common.theming

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * SkyFox brand theme — extracted directly from logo SVGs.
 * Dark: navy #0b1220, sky blue #4db3ec, fox orange #f96316.
 * Light: white bg, same accent pair.
 */
object AplColorsFox {

    // region Light Default
    val lightDefault = lightColorScheme(
        primary          = Color(0xFF1a5f8a),   // darkened sky blue for light bg contrast
        onPrimary        = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFbde3ff),
        onPrimaryContainer = Color(0xFF001e30),
        secondary        = Color(0xFF1e4a6e),
        onSecondary      = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFc8dff2),
        onSecondaryContainer = Color(0xFF0d2535),
        tertiary         = Color(0xFFc44e0d),   // darkened fox orange for light bg
        onTertiary       = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFffdbc9),
        onTertiaryContainer = Color(0xFF2a1200),
        error            = Color(0xFFBB1B1B),
        onError          = Color(0xFFFFFFFF),
        errorContainer   = Color(0xFFffdad6),
        onErrorContainer = Color(0xFF410002),
        background       = Color(0xFFf8fafc),
        onBackground     = Color(0xFF0b1220),
        surface          = Color(0xFFf8fafc),
        onSurface        = Color(0xFF0b1220),
        surfaceVariant   = Color(0xFFdce6f0),
        onSurfaceVariant = Color(0xFF2a3d52),
        outline          = Color(0xFF4d7a9e),
        outlineVariant   = Color(0xFFbcd0e4),
        inverseSurface   = Color(0xFF0b1220),
        inverseOnSurface = Color(0xFFf8fafc),
        inversePrimary   = Color(0xFF4db3ec),
        surfaceDim       = Color(0xFFd8e4ee),
        surfaceBright    = Color(0xFFf8fafc),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow    = Color(0xFFf2f7fb),
        surfaceContainer       = Color(0xFFecf3f8),
        surfaceContainerHigh   = Color(0xFFe5eff5),
        surfaceContainerHighest = Color(0xFFdce6f0),
    )
    // endregion

    // region Dark Default — navy bg, sky blue primary, fox orange tertiary
    val darkDefault = darkColorScheme(
        primary          = Color(0xFF4db3ec),   // #4db3ec from logo
        onPrimary        = Color(0xFF001e30),
        primaryContainer = Color(0xFF1a4a6e),
        onPrimaryContainer = Color(0xFFbde3ff),
        secondary        = Color(0xFF94b8d0),
        onSecondary      = Color(0xFF0d2535),
        secondaryContainer = Color(0xFF142c4a),  // #142c4a from logo dark bg
        onSecondaryContainer = Color(0xFFc8dff2),
        tertiary         = Color(0xFFf96316),    // #f96316 from logo
        onTertiary       = Color(0xFF2a1200),
        tertiaryContainer = Color(0xFF4a2200),
        onTertiaryContainer = Color(0xFFffdbc9),
        error            = Color(0xFFFF7164),
        onError          = Color(0xFF4A0002),
        errorContainer   = Color(0xFF8c0009),
        onErrorContainer = Color(0xFFffb4ab),
        background       = Color(0xFF0b1220),    // #0b1220 from logo
        onBackground     = Color(0xFFf8fafc),    // #f8fafc from logo
        surface          = Color(0xFF0b1220),
        onSurface        = Color(0xFFf8fafc),
        surfaceVariant   = Color(0xFF142c4a),
        onSurfaceVariant = Color(0xFF8aafc8),
        outline          = Color(0xFF4d7a9e),
        outlineVariant   = Color(0xFF1e3a55),
        inverseSurface   = Color(0xFFf8fafc),
        inverseOnSurface = Color(0xFF0b1220),
        inversePrimary   = Color(0xFF1a5f8a),
        surfaceDim       = Color(0xFF060a10),
        surfaceBright    = Color(0xFF1a2d42),
        surfaceContainerLowest  = Color(0xFF060a10),
        surfaceContainerLow     = Color(0xFF0d1828),
        surfaceContainer        = Color(0xFF111f30),
        surfaceContainerHigh    = Color(0xFF162438),
        surfaceContainerHighest = Color(0xFF1c2d40),
    )
    // endregion

    val lightMediumContrast = lightDefault
    val lightHighContrast   = lightDefault
    val darkMediumContrast  = darkDefault
    val darkHighContrast    = darkDefault
}
