package de.taymaerz.skyfox.common.theming

import de.taymaerz.skyfox.R
import de.taymaerz.skyfox.common.preferences.EnumPreference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeStyle(override val labelRes: Int) : EnumPreference<ThemeStyle> {
    @SerialName("DEFAULT") DEFAULT(R.string.ui_theme_style_default_label),
    @SerialName("MATERIAL_YOU") MATERIAL_YOU(R.string.ui_theme_style_materialyou_label),
    @SerialName("MEDIUM_CONTRAST") MEDIUM_CONTRAST(R.string.ui_theme_style_medium_contrast_label),
    @SerialName("HIGH_CONTRAST") HIGH_CONTRAST(R.string.ui_theme_style_high_contrast_label),
    ;
}
