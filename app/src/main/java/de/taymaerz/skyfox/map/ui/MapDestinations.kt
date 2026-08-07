package de.taymaerz.skyfox.map.ui

import de.taymaerz.skyfox.common.navigation.NavigationDestination
import de.taymaerz.skyfox.map.core.MapOptions
import kotlinx.serialization.Serializable

@Serializable
data class DestinationMap(
    val mapOptions: MapOptions? = null,
) : NavigationDestination
