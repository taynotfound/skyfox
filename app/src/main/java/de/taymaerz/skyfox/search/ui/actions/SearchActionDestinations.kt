package de.taymaerz.skyfox.search.ui.actions

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data class DestinationSearchAction(
    val hex: String,
) : NavigationDestination
