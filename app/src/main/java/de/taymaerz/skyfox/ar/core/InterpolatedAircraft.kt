package de.taymaerz.skyfox.ar.core

import de.taymaerz.skyfox.main.core.aircraft.Aircraft

data class InterpolatedAircraft(
    val source: Aircraft,
    val interpolatedLat: Double,
    val interpolatedLon: Double,
    val altitudeFt: Int?,
    val distanceM: Double,
    val ageSec: Float,
)
