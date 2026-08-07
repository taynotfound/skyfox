package de.taymaerz.skyfox.search.ui

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import kotlinx.serialization.Serializable

@Serializable
data class DestinationSearch(
    val targetHexes: List<String>? = null,
    val targetSquawks: List<String>? = null,
    val targetCallsigns: List<String>? = null,
) : NavigationDestination
