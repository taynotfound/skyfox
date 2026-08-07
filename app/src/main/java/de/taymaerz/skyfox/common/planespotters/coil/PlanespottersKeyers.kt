package de.taymaerz.skyfox.common.planespotters.coil

import coil3.key.Keyer
import coil3.request.Options
import de.taymaerz.skyfox.main.core.aircraft.Aircraft

class PlanespottersKeyer : Keyer<Aircraft> {
    override fun key(data: Aircraft, options: Options): String {
        return "aircraft-${data.hex}"
    }
}

class PlanespottersThumbnailKeyer : Keyer<AircraftThumbnailQuery> {
    override fun key(data: AircraftThumbnailQuery, options: Options): String {
        return "planespotters-${data.hex}-${data.large}"
    }
}
