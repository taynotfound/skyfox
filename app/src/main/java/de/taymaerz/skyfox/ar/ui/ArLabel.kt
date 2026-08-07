package de.taymaerz.skyfox.ar.ui

import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.aircraft.Callsign
import de.taymaerz.skyfox.main.core.aircraft.Registration

sealed interface RouteUiState {
    data object Idle : RouteUiState
    data object Loading : RouteUiState
    data class Ready(val text: String) : RouteUiState
    data object Unavailable : RouteUiState
}

data class ArLabel(
    val hex: AircraftHex,
    val callsign: Callsign?,
    val registration: Registration?,
    val description: String?,
    val altitudeFt: Int?,
    val speedKts: Float?,
    val distanceM: Double,
    val screenXNorm: Float,
    val screenYNorm: Float,
    val routeState: RouteUiState = RouteUiState.Idle,
)
