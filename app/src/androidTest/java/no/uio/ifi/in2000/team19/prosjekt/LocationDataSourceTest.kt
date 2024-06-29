package no.uio.ifi.in2000.team19.prosjekt

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastDataSource
import no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast.LocationForecast
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Mostly just tests that the API actually works, but also
 * tests that behaviour is as expected.
 *
 * A better test should mock clients and more: https://ktor.io/docs/client-testing.html#add_dependencies
 * Making actual network tests are not recomended, and as technical debt we should ideally create a Unit Test
 * and change dataSource to accept the KTOR http client as a parameter, so we can mock it in the unit test.
 * + technical debt on writing better tests for datasource.
 *
 * Needs to be an instrumented test to actually make network calls
 *
 * But its also nice to test the ifi proxy / MET api in our opinion.*/
class LocationDataSourceTest {


    @Test
    fun get_valid_result_with_valid_coordinates() {

        val dataSource = LocationForecastDataSource()

        // ARRANGE
        val latitude = "50"
        val longitude = "50"
        var apiResult: LocationForecast?


        // ACT
        runBlocking {
            apiResult = dataSource.getLocationForecast(latitude, longitude)
        }
        // ASSERT
        assertNotNull(apiResult)

    }

    @Test
    fun get_null_with_invalid_coordinates() {

        val dataSource = LocationForecastDataSource()

        // ARRANGE
        val latitude = "0"
        val longitude = "9999"
        var apiResult: LocationForecast?


        // ACT
        runBlocking {
            apiResult = dataSource.getLocationForecast(latitude, longitude)
        }
        // ASSERT
        assertEquals(null, apiResult)

    }

}