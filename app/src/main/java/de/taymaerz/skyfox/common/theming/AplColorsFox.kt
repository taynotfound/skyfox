package de.taymaerz.skyfox.common.theming

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * SkyFox brand theme — warm oranges on warm dark surfaces.
 * Matches the landing site palette (#f97316 accent, #0c0b0a backgrounds).
 */
object AplColorsFox {

    // region Light Default
    val lightDefault = lightColorScheme(
        primary = Color(0xFF9A4515),
        onPrimary = Color(0xFFFFF8F5),
        primaryContainer = Color(0xFFFFB68C),
        onPrimaryContainer = Color(0xFF5C2600),
        secondary = Color(0xFF755846),
        onSecondary = Color(0xFFFFF8F5),
        secondaryContainer = Color(0xFFFFDBC7),
        onSecondaryContainer = Color(0xFF5C4130),
        tertiary = Color(0xFF616138),
        onTertiary = Color(0xFFFFFFF2),
        tertiaryContainer = Color(0xFFE7E6B0),
        onTertiaryContainer = Color(0xFF494922),
        error = Color(0xFFBB1B1B),
        onError = Color(0xFFFFF7F6),
        errorContainer = Color(0xFFFE4E44),
        onErrorContainer = Color(0xFF570003),
        background = Color(0xFFFDF8F5),
        onBackground = Color(0xFF34302D),
        surface = Color(0xFFFDF8F5),
        onSurface = Color(0xFF34302D),
        surfaceVariant = Color(0xFFE8E1DC),
        onSurfaceVariant = Color(0xFF615C58),
        outline = Color(0xFF7D7873),
        outlineVariant = Color(0xFFB5AFAA),
        inverseSurface = Color(0xFF110E0C),
        inverseOnSurface = Color(0xFFA09B96),
        inversePrimary = Color(0xFFFFB68C),
        surfaceDim = Color(0xFFDED8D4),
        surfaceBright = Color(0xFFFDF8F5),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF7F2EE),
        surfaceContainer = Color(0xFFF1ECE8),
        surfaceContainerHigh = Color(0xFFEBE6E2),
        surfaceContainerHighest = Color(0xFFE8E1DC),
    )
    // endregion

    // region Dark Default — warm dark, orange accent
    val darkDefault = darkColorScheme(
        primary = Color(0xFFFB923C),
        onPrimary = Color(0xFF4A1D00),
        primaryContainer = Color(0xFF9A3D0A),
        onPrimaryContainer = Color(0xFFFFEDE2),
        secondary = Color(0xFFE5C0A9),
        onSecondary = Color(0xFF432B1B),
        secondaryContainer = Color(0xFF5C4130),
        onSecondaryContainer = Color(0xFFFFDBC7),
        tertiary = Color(0xFFCBCA96),
        onTertiary = Color(0xFF32320F),
        tertiaryContainer = Color(0xFF494922),
        onTertiaryContainer = Color(0xFFE7E6B0),
        error = Color(0xFFFF7164),
        onError = Color(0xFF4A0002),
        errorContainer = Color(0xFFAC0C12),
        onErrorContainer = Color(0xFFFFB8B0),
        background = Color(0xFF16120F),
        onBackground = Color(0xFFE9E5E1),
        surface = Color(0xFF16120F),
        onSurface = Color(0xFFE9E5E1),
        surfaceVariant = Color(0xFF2E2925),
        onSurfaceVariant = Color(0xFF9C948C),
        outline = Color(0xFF837B74),
        outlineVariant = Color(0xFF4A443F),
        inverseSurface = Color(0xFFFDF8F5),
        inverseOnSurface = Color(0xFF56504B),
        inversePrimary = Color(0xFF9A4515),
        surfaceDim = Color(0xFF16120F),
        surfaceBright = Color(0xFF3B342F),
        surfaceContainerLowest = Color(0xFF0C0B0A),
        surfaceContainerLow = Color(0xFF1E1915),
        surfaceContainer = Color(0xFF241F1A),
        surfaceContainerHigh = Color(0xFF2B2520),
        surfaceContainerHighest = Color(0xFF362F29),
    )
    // endregion

    // Contrast variants fall back to default until dedicated palettes exist
    val lightMediumContrast = lightDefault
    val lightHighContrast = lightDefault
    val darkMediumContrast = darkDefault
    val darkHighContrast = darkDefault
}
