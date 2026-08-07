package de.taymaerz.skyfox.main.core.api

import android.location.Location


suspend fun AirplanesLiveEndpoint.getByLocation(location: Location, radiusInMeter: Long) =
    getByLocation(location.latitude, location.longitude, radiusInMeter)