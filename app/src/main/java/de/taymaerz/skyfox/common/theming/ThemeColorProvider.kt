package de.taymaerz.skyfox.common.theming

import androidx.compose.material3.ColorScheme

object ThemeColorProvider {

    fun getLightColorScheme(color: ThemeColor, style: ThemeStyle): ColorScheme = when (color) {
        ThemeColor.FOX -> when (style) {
            ThemeStyle.MEDIUM_CONTRAST -> AplColorsFox.lightMediumContrast
            ThemeStyle.HIGH_CONTRAST -> AplColorsFox.lightHighContrast
            else -> AplColorsFox.lightDefault
        }

        ThemeColor.BLUE, ThemeColor.AMOLED -> when (style) {
            ThemeStyle.MEDIUM_CONTRAST -> AplColorsBlue.lightMediumContrast
            ThemeStyle.HIGH_CONTRAST -> AplColorsBlue.lightHighContrast
            else -> AplColorsBlue.lightDefault
        }
    }

    fun getDarkColorScheme(color: ThemeColor, style: ThemeStyle): ColorScheme = when (color) {
        ThemeColor.FOX -> when (style) {
            ThemeStyle.MEDIUM_CONTRAST -> AplColorsFox.darkMediumContrast
            ThemeStyle.HIGH_CONTRAST -> AplColorsFox.darkHighContrast
            else -> AplColorsFox.darkDefault
        }

        ThemeColor.BLUE -> when (style) {
            ThemeStyle.MEDIUM_CONTRAST -> AplColorsBlue.darkMediumContrast
            ThemeStyle.HIGH_CONTRAST -> AplColorsBlue.darkHighContrast
            else -> AplColorsBlue.darkDefault
        }

        ThemeColor.AMOLED -> when (style) {
            ThemeStyle.MEDIUM_CONTRAST -> AplColorsAmoled.darkMediumContrast
            ThemeStyle.HIGH_CONTRAST -> AplColorsAmoled.darkHighContrast
            else -> AplColorsAmoled.darkDefault
        }
    }
}
