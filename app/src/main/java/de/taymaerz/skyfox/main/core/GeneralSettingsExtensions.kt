package de.taymaerz.skyfox.main.core

import de.taymaerz.skyfox.common.theming.ThemeColor
import de.taymaerz.skyfox.common.theming.ThemeMode
import de.taymaerz.skyfox.common.theming.ThemeStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class ThemeState(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val style: ThemeStyle = ThemeStyle.DEFAULT,
    val color: ThemeColor = ThemeColor.BLUE,
)

val GeneralSettings.themeState: Flow<ThemeState>
    get() = combine(
        themeMode.flow,
        themeStyle.flow,
        themeColor.flow,
    ) { mode, style, color ->
        ThemeState(mode, style, color)
    }