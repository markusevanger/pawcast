package no.uio.ifi.in2000.team19.prosjekt.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import io.ktor.util.appendIfNameAbsent
import no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast.LocationForecast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationForecastDataSource @Inject constructor() {

    private val client = HttpClient {
        defaultRequest {
            url("https://gw-uio.intark.uh-it.no/in2000/")
            headers.appendIfNameAbsent("X-Gravitee-API-key", ApiKeys.PROXY_KEY)
        }
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getLocationForecast(
        latitude: String,
        longitude: String,
    ): LocationForecast? {

        return try {
            Log.d("debug", "Making api call with coords: $latitude, $longitude")

            val path =
                "${ApiUrls.LOCATIONFORECAST_EDR_API}position?coords=POINT($longitude+$latitude)"

            val result = client.get(path)

            result.body<LocationForecast>()

        } catch (e: Exception) {
            null
        }

    }


}