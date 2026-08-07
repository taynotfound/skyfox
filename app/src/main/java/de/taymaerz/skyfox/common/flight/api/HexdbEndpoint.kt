package de.taymaerz.skyfox.common.flight.api

import dagger.Reusable
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.VERBOSE
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Inject

@Reusable
class HexdbEndpoint @Inject constructor(
    private val baseClient: OkHttpClient,
    private val jsonConverterFactory: Converter.Factory,
    private val dispatcherProvider: DispatcherProvider,
) {

    internal var baseUrl: String = "https://hexdb.io/"

    private val api: HexdbApi by lazy {
        Retrofit.Builder()
            .client(baseClient.newBuilder().build())
            .baseUrl(baseUrl)
            .addConverterFactory(jsonConverterFactory)
            .build()
            .create(HexdbApi::class.java)
    }

    suspend fun getByCallsign(callsign: String): HexdbApi.RouteResponse? = withContext(dispatcherProvider.IO) {
        log(TAG) { "getByCallsign(callsign=$callsign)" }

        val response = api.getByCallsign(callsign)
            .also { log(TAG, VERBOSE) { "getByCallsign($callsign) -> $it" } }

        if (response.route.isNullOrBlank()) null else response
    }

    suspend fun getAircraft(hex: String): HexdbApi.AircraftResponse? = withContext(dispatcherProvider.IO) {
        log(TAG) { "getAircraft(hex=$hex)" }
        val response = api.getAircraft(hex)
        if (response.registration.isNullOrBlank() && response.type.isNullOrBlank()) null else response
    }

    companion object {
        private val TAG = logTag("Flight", "Hexdb", "Endpoint")
    }
}
