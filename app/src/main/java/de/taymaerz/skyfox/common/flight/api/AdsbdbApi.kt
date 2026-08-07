package de.taymaerz.skyfox.common.flight.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Path

interface AdsbdbApi {

    @Serializable
    data class CallsignResponse(
        @SerialName("response") val response: ResponseData? = null,
    )

    @Serializable
    data class ResponseData(
        @SerialName("flightroute") val flightroute: JsonElement? = null,
    )

    @Serializable
    data class FlightRouteData(
        @SerialName("callsign") val callsign: String? = null,
        @SerialName("callsign_icao") val callsignIcao: String? = null,
        @SerialName("callsign_iata") val callsignIata: String? = null,
        @SerialName("airline") val airline: AirlineData? = null,
        @SerialName("origin") val origin: AirportData? = null,
        @SerialName("destination") val destination: AirportData? = null,
    )

    @Serializable
    data class AirlineData(
        @SerialName("name") val name: String? = null,
        @SerialName("icao") val icao: String? = null,
        @SerialName("iata") val iata: String? = null,
        @SerialName("country") val country: String? = null,
        @SerialName("callsign") val callsign: String? = null,
    )

    @Serializable
    data class AirportData(
        @SerialName("country_iso_name") val countryIsoName: String? = null,
        @SerialName("country_name") val countryName: String? = null,
        @SerialName("elevation") val elevation: Int? = null,
        @SerialName("iata_code") val iataCode: String? = null,
        @SerialName("icao_code") val icaoCode: String? = null,
        @SerialName("latitude") val latitude: Double? = null,
        @SerialName("longitude") val longitude: Double? = null,
        @SerialName("municipality") val municipality: String? = null,
        @SerialName("name") val name: String? = null,
    )

    @Serializable
    data class AircraftResponse(
        @SerialName("response") val response: AircraftResponseData? = null,
    )

    @Serializable
    data class AircraftResponseData(
        @SerialName("aircraft") val aircraft: JsonElement? = null,
    )

    @Serializable
    data class AircraftData(
        @SerialName("type") val type: String? = null,
        @SerialName("icao_type") val icaoType: String? = null,
        @SerialName("manufacturer") val manufacturer: String? = null,
        @SerialName("mode_s") val modeS: String? = null,
        @SerialName("registration") val registration: String? = null,
        @SerialName("registered_owner_country_name") val ownerCountry: String? = null,
        @SerialName("registered_owner") val owner: String? = null,
        @SerialName("url_photo") val photoUrl: String? = null,
        @SerialName("url_photo_thumbnail") val photoThumbUrl: String? = null,
    )

    @GET("v0/callsign/{callsign}")
    suspend fun getByCallsign(@Path("callsign") callsign: String): CallsignResponse

    @GET("v0/aircraft/{hex}")
    suspend fun getAircraft(@Path("hex") hex: String): AircraftResponse
}
