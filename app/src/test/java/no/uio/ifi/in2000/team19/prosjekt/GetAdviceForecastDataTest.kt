package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.data.AdviceFunctions
import no.uio.ifi.in2000.team19.prosjekt.model.dto.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class GetAdviceForecastDataTest {

    @Test
    fun getAdviceForecastDataIsCorrect() {

        // Arrange and act
        val generalForecast = GeneralForecast(
            14.6,
            4.5,
            "partlycloudy",
            "12",
            LocalDateTime.of(2024, 3, 1, 0, 0),
            1.8,
            44.2,
            2.7
        )


        val expectedAdviceForecast =
            AdviceForecast(14.6, 44.2, 1.8, 2.7, LocalDateTime.of(2024, 3, 1, 0, 0), "12")
        val result = AdviceFunctions.getAdviceForecastData(generalForecast)

        // Assert
        assertEquals(expectedAdviceForecast, result)
    }
}