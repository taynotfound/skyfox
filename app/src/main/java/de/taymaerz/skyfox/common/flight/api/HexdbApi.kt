package de.taymaerz.skyfox.common.flight.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path

interface HexdbApi {

    @Serializable
    data class RouteResponse(
        @SerialName("flight") val flight: String? = null,
        @SerialName("route") val route: String? = null,
    )

    @Serializable
    data class AircraftResponse(
        @SerialName("ModeS") val modeS: String? = null,
        @SerialName("Registration") val registration: String? = null,
        @SerialName("Manufacturer") val manufacturer: String? = null,
        @SerialName("ICAOTypeCode") val icaoTypeCode: String? = null,
        @SerialName("Type") val type: String? = null,
        @SerialName("RegisteredOwners") val registeredOwners: String? = null,
    )

    @GET("api/v1/route/icao/{callsign}")
    suspend fun getByCallsign(@Path("callsign") callsign: String): RouteResponse

    @GET("api/v1/aircraft/{hex}")
    suspend fun getAircraft(@Path("hex") hex: String): AircraftResponse
}
