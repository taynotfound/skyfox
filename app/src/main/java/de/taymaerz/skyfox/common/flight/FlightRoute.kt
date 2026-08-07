package de.taymaerz.skyfox.common.flight

import de.taymaerz.skyfox.common.flight.db.AirportEntity
import de.taymaerz.skyfox.common.flight.db.FlightRouteEntity
import java.time.Instant

data class FlightRoute(
    val callsign: String,
    val origin: Airport?,
    val destination: Airport?,
    val airlineName: String? = null,
    val seenAt: Instant,
)

data class Airport(
    val icaoCode: String,
    val iataCode: String?,
    val name: String?,
    val countryName: String?,
    val municipality: String? = null,
) {
    val displayLabel: String
        get() = iataCode ?: icaoCode

    val routeDisplayText: String
        get() = if (name != null) "$name ($displayLabel)" else displayLabel
}

fun FlightRouteEntity.toDomain(
    originAirport: AirportEntity?,
    destinationAirport: AirportEntity?,
) = FlightRoute(
    callsign = callsign,
    airlineName = airlineName,
    origin = originIcao?.let { icao ->
        Airport(
            icaoCode = icao,
            iataCode = originAirport?.iataCode,
            name = originAirport?.name,
            countryName = originAirport?.country,
            municipality = originAirport?.municipality,
        )
    },
    destination = destinationIcao?.let { icao ->
        Airport(
            icaoCode = icao,
            iataCode = destinationAirport?.iataCode,
            name = destinationAirport?.name,
            countryName = destinationAirport?.country,
            municipality = destinationAirport?.municipality,
        )
    },
    seenAt = seenAt,
)
