package de.taymaerz.skyfox.common.planespotters

import de.taymaerz.skyfox.common.planespotters.coil.AircraftThumbnailQuery
import de.taymaerz.skyfox.main.core.aircraft.Aircraft

fun Aircraft.toPlanespottersQuery(large: Boolean = false) = AircraftThumbnailQuery(
    hex = this.hex,
    registration = this.registration,
    large = large,
)
