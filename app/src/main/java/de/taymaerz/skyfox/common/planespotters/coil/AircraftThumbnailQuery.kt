package de.taymaerz.skyfox.common.planespotters.coil

import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.aircraft.Registration

data class AircraftThumbnailQuery(
    val hex: AircraftHex,
    val registration: Registration? = null,
    val large: Boolean = false,
    val photoIndex: Int = 0,
)
