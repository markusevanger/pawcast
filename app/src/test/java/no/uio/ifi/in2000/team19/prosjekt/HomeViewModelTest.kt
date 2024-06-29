package no.uio.ifi.in2000.team19.prosjekt

import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel.Companion.getWhichDogTypeSymbol
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class HomeViewModelTest {

    @Test
    fun testSunnyDog() {
        val weather = GeneralForecast(
            symbol = "day",
            temperature = 20.0,
            wind = 0.0,
            precipitation = 0.0,
            thunderProbability = 0.0,
            uvIndex = 0.0,
            timeFetched = LocalDateTime.now(),
            hour = "12"
        )
        val expectedDrawable = R.drawable.dog_sunny
        val actualDrawable = getWhichDogTypeSymbol(weather)
        assertEquals(expectedDrawable, actualDrawable)
    }

    @Test
    fun testColdDog() {
        val weather = GeneralForecast(
            symbol = "day",
            temperature = -5.0,
            wind = 0.0,
            precipitation = 0.0,
            thunderProbability = 0.0,
            uvIndex = 0.0,
            timeFetched = LocalDateTime.now(),
            hour = "12"
        )
        val expectedDrawable = R.drawable.dog_cold
        val actualDrawable = getWhichDogTypeSymbol(weather)
        assertEquals(expectedDrawable, actualDrawable)
    }

    @Test
    fun testWindyDog() {
        val weather = GeneralForecast(
            symbol = "day",
            temperature = 10.0,
            wind = 6.0,
            precipitation = 0.0,
            thunderProbability = 0.0,
            uvIndex = 0.0,
            timeFetched = LocalDateTime.now(),
            hour = "12"
        )
        val expectedDrawable = R.drawable.dog_wind
        val actualDrawable = getWhichDogTypeSymbol(weather)
        assertEquals(expectedDrawable, actualDrawable)
    }

    @Test
    fun whenRainingAndThundering_IsShowingThundering() {
        val weather = GeneralForecast(
            symbol = "thundering_day", // viewmodel only looks at this.
            temperature = 10.0,
            wind = 4.0,
            precipitation = 40.0,
            thunderProbability = 80.0,
            uvIndex = 0.0,
            timeFetched = LocalDateTime.now(),
            hour = "12"
        )
        val expectedDrawable = R.drawable.dog_thunder
        val actualDrawable = getWhichDogTypeSymbol(weather)
        assertEquals(expectedDrawable, actualDrawable)
    }
}