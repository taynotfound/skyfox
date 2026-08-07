package de.taymaerz.skyfox.common.planespotters.api

import dagger.Reusable
import de.taymaerz.skyfox.common.coroutine.DispatcherProvider
import de.taymaerz.skyfox.common.debug.logging.Logging.Priority.VERBOSE
import de.taymaerz.skyfox.common.debug.logging.log
import de.taymaerz.skyfox.common.debug.logging.logTag
import de.taymaerz.skyfox.main.core.aircraft.AircraftHex
import de.taymaerz.skyfox.main.core.aircraft.Registration
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Inject


@Reusable
class PlanespottersEndpoint @Inject constructor(
    private val baseClient: OkHttpClient,
    private val jsonConverterFactory: Converter.Factory,
    private val dispatcherProvider: DispatcherProvider,
) {

    private val api: PlanespottersApi by lazy {
        val configHttpClient = baseClient.newBuilder().apply {

        }.build()

        Retrofit.Builder()
            .client(configHttpClient)
            .baseUrl("https://api.planespotters.net/")
            .addConverterFactory(jsonConverterFactory)
            .build()
            .create(PlanespottersApi::class.java)
    }

    suspend fun getPhotosByHex(
        hex: AircraftHex,
    ): List<PlanespottersApi.Photo> = withContext(dispatcherProvider.IO) {
        log(TAG) { "getPhotosByHex(hex=$hex)" }

        api.getPhotosByHex(hex)
            .also { log(TAG, VERBOSE) { "getPhotosByHex(hex=$hex) -> $it" } }
            .photos
    }

    suspend fun getPhotosByRegistration(
        registration: Registration,
    ): List<PlanespottersApi.Photo> = withContext(dispatcherProvider.IO) {
        log(TAG) { "getPhotosByRegistration(registration=$registration)" }

        api.getPhotosByRegistration(registration)
            .also { log(TAG, VERBOSE) { "getPhotosByRegistration(registration=$registration) -> $it" } }
            .photos
    }

    companion object {
        private val TAG = logTag("Planespotters", "Endpoint")
    }
}
