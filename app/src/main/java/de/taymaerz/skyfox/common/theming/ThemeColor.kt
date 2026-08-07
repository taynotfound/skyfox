package de.taymaerz.skyfox.common.theming

import de.taymaerz.skyfox.R
import de.taymaerz.skyfox.common.preferences.EnumPreference
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ThemeColor(override val labelRes: Int) : EnumPreference<ThemeColor> {
    @SerialName("BLUE") BLUE(R.string.ui_theme_color_blue_label),
    @SerialName("AMOLED") AMOLED(R.string.ui_theme_color_amoled_label),
    ;
}
