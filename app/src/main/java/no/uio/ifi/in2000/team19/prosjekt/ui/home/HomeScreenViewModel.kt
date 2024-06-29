package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Location
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.createTemporaryUserinfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.ErrorReasons
import no.uio.ifi.in2000.team19.prosjekt.model.dto.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.dto.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.ForecastTypes
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import java.io.IOException
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDateTime
import javax.inject.Inject


sealed interface DataState {
    data object Success : DataState
    data object Loading : DataState
    data class Error(val errorReason: ErrorReasons) : DataState
}

data class BestTimesForWalk(
    var morning: String,
    var midday: String,
    var evening: String
)

/** Collected into one ui state as state updates happen mostly together.
 * Splitting them into multiple StateFlows are therefore unnecessary */
data class HomeUiState(
    var dataState: DataState,
    var advice: List<Advice>,
    var graphModel: CartesianChartModelProducer,
    var bestTimesForWalk: BestTimesForWalk,
    var scoreAtIndexZero: Int,
    var location: Location,
    var userInfo: UserInfo,
    var weather: GeneralForecast,
    var dogImageId: Int
)


@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val locationForecastRepository: LocationForecastRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(
        HomeUiState(
            dataState = DataState.Loading,
            /**Contains all advice cards */
            advice = emptyList(),
            /** Contains graph data */
            graphModel = CartesianChartModelProducer.build(),
            /** Contains the best time for a trip for morning, midday and evening based on score. */
            bestTimesForWalk = BestTimesForWalk("", "", ""),
            /** Contains the value of graphs score on index 0. Used to set graph color to different color based on this value. */
            scoreAtIndexZero = 0,
            /** Contains the users location. Exposed to UI so show location in HomeScreen. */
            location = Location(0, "not loaded", "not loaded", "", ""),
            /** Is exposed to UI to show username and dog name. */
            userInfo = createTemporaryUserinfo(),
            /** Used to show current weather (temperature and icon). */
            weather = GeneralForecast(0.0, 0.0, "", "", LocalDateTime.now(), 0.0, 0.0, 0.0),
            /** Is used to determine which to dog show in home screen. */
            dogImageId = R.drawable.dog_normal,
        )
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {

        try {
            viewModelScope.launch(Dispatchers.IO) {
                settingsRepository.getLocation().collect { location ->
                    updateLocation(location)
                    loadWeatherForecast()
                }

            }
        } catch (e: IOException) {
            updateDataState(DataState.Error(ErrorReasons.DATABASE))
        } catch (e: Exception) {
            updateDataState(DataState.Error(ErrorReasons.UNKNOWN))
        }
    }

    fun loadWeatherForecast() {

        var generalForecast: ForecastTypes? = null

        val uiState = _uiState.value
        val location = uiState.location


        updateDataState(DataState.Loading)

        viewModelScope.launch(Dispatchers.IO) {

            try {
                generalForecast = locationForecastRepository.getGeneralForecast(
                    location.latitude,
                    location.longitude,
                    2
                )


            } catch (e: IOException) {
                updateDataState(DataState.Error(ErrorReasons.INTERRUPTION))
                Log.e("Error", e.toString())
            } catch (e: UnresolvedAddressException) {
                updateDataState(DataState.Error(ErrorReasons.INTERNET))
                Log.e("Error", e.toString())
            } catch (e: Exception) {
                updateDataState(DataState.Error(ErrorReasons.UNKNOWN))
                Log.e("Error", e.toString())
            }

            // This is how we handle connectivity. Instead of using connectivity manager (which seems like the correct way).
            // Essentially we catch our errors, and use the error message to decode if we lost internet, and update
            // our screen to reflect this. This solution is a LOT simpler, although not as flexible. Landed on this
            // mostly due to time constraints / focus on other issues under development, and this works and is robust enough.

            // >>  This is one of the areas of technical debt to focus on for future. <<

            if (generalForecast != null) updateUiStateBasedOnForecast(generalForecast!!)
            else updateDataState(DataState.Error(ErrorReasons.UNKNOWN))

        }
    }

    private fun updateUiStateBasedOnForecast(generalForecast: ForecastTypes) {

        val uiState = _uiState.value
        val weatherNow = generalForecast.general[0]

        updateWeather(weatherNow)
        updateDogImageId(getWhichDogTypeSymbol(weatherNow))
        updateUserInfo(settingsRepository.getUserInfo())
        updateAdvice(locationForecastRepository.getAdvice(generalForecast, uiState.userInfo))


        // Graph values
        val bestTimesResult = makeHourlyScoreBasedOnForecast(locationForecastRepository.getAdviceForecastList(generalForecast))
        val graphScores = bestTimesResult.first
        val bestTimesForWalk = bestTimesResult.second

        updateScoreAtIndexZero(graphScores[0])
        updateBestTimesForWalk(bestTimesForWalk)
        updateGraphValues(graphScores)

        updateDataState(DataState.Success) // Signal to Ui that all ui items are done updating and to show your screen.
    }


    /** Used by advice screen. Really simple so didnt move to other viewmodel, as that would require sharing existing advice with that viewmodel
     * or changing overall architecture. If point is to expand on advice screens in future, there is techincal debt to modularizing AdviceScreen to its own viewmodel
     * and its content. */
    fun collectAdviceById(id: Int): Advice {
        return _uiState.value.advice[id]
    }


    /*
    *
    * Functions to update the items contained within the HomeUiState class. Since we only have one uiState MutableStateFlow,
    * there is no way for compose to know when the HomeUiState class values has changed. We used to have all of these values split
    * into different StateFlows, but all of these data items are needed at the same time, and change together. For easier readability
    * update are moved into their own functions.
    *
    *  */
    private fun updateDataState(dataState: DataState) {
        _uiState.update { it.copy(dataState = dataState) }
        Log.d("debug", "HomeScreen data state was updated: $dataState")
    }

    private fun updateAdvice(adviceList: List<Advice>) {
        _uiState.update { it.copy(advice = adviceList) }
    }


    private fun updateBestTimesForWalk(bestTimes: BestTimesForWalk) {
        _uiState.update { it.copy(bestTimesForWalk = bestTimes) }
    }

    private fun updateScoreAtIndexZero(score: Int) {
        _uiState.update { it.copy(scoreAtIndexZero = score) }
    }

    private fun updateGraphValues(yAxisValue: List<Int>) {
        _uiState.value.graphModel.tryRunTransaction {
            lineSeries {
                series(
                    y = yAxisValue
                )
            }
        }
    }

    private fun updateLocation(location: Location) {
        _uiState.update { it.copy(location = location) }
    }

    private fun updateUserInfo(userInfo: UserInfo) {
        _uiState.update { it.copy(userInfo = userInfo) }
    }

    private fun updateWeather(weather: GeneralForecast) {
        _uiState.update { it.copy(weather = weather) }
    }

    private fun updateDogImageId(imageId: Int) {
        _uiState.update { it.copy(dogImageId = imageId) }
    }

    fun checkIfUiStateIsError(): Boolean {
        return _uiState.value.dataState is DataState.Error
    }

    /** Functions moved to companion mainly to make testing easier, but is also
     * a nice way of modularizing the app and accessing this function whenever needed elsewhere. */
    companion object {

        /**
         *
         * Creates graph data based on temperature, precipitation and UVLimit
         * we are using AdviceForecast because we only need temp, precipitation and UVLimit
         *
         * Parameter: a list of (advice) forecast objects that each represent one hour of the day
         * Returns: A Pair containing a list with a score for each hour and an instance of BestTimesForWalk
         *
         */
        private fun makeHourlyScoreBasedOnForecast(forecasts: List<AdviceForecast>): Pair<List<Int>, BestTimesForWalk> {

            val overallRatingList = mutableListOf<Int>()
            val currentHours = mutableListOf<String>()
            var bestRatingMorning = 0
            var bestRatingMidday = 0
            var bestRatingEvening = 0

            val bestTimesForWalk = BestTimesForWalk(
                morning = "",
                midday = "",
                evening = ""
            )

            forecasts.forEach { forecast ->

                val hourOfDay = forecast.time
                currentHours.add(hourOfDay)

                val tempRating = getRatingBasedOnLimitsMap(forecast.temperature, LimitMaps.tempLimitMap)
                val percRating = getRatingBasedOnLimitsMap(forecast.precipitation, LimitMaps.precipitationLimitMap)
                val uvRating = getRatingBasedOnLimitsMap(forecast.uvIndex, LimitMaps.uvLimitMap)

                val ratings = listOf(tempRating, percRating, uvRating)

                var overallRating: Int = (tempRating + percRating + uvRating) / 3

                ratings.forEach {
                    if (it < 3) {
                        overallRating = it
                    }
                }

                // Find highest score at morning, midday and evening.
                val hour = hourOfDay.toInt()

                // Morning
                if (hour in 5..10) {
                    if (overallRating >= bestRatingMorning && overallRating > 4) {
                        bestRatingMorning = overallRating
                        bestTimesForWalk.morning = hourOfDay
                    }
                }

                // Midday
                else if (hour in 10..18) {
                    if (overallRating >= bestRatingMidday && overallRating > 4) {
                        bestRatingMidday = overallRating
                        bestTimesForWalk.midday = hourOfDay
                    }
                }

                // Evening
                else if (hour in 18..22) {
                    if (overallRating >= bestRatingEvening && overallRating > 4) {
                        bestRatingEvening = overallRating
                        bestTimesForWalk.evening = hourOfDay
                    }
                }

                overallRatingList.add(overallRating)
            }
            // x axis is defined in UI layer based on current time + index of y points.
            return Pair(overallRatingList, bestTimesForWalk) // y axis data
        }


        /** Function is used to fetch the rating of a given weather specification */
        private fun getRatingBasedOnLimitsMap(weatherTypeValue: Double, limitsMap: HashMap<List<Double>, Int>): Int {

            for ((key, value) in limitsMap) {
                if (weatherTypeValue in key[0]..key[1]) {
                    return value
                }
            }
            return 1
        }


        fun getWhichDogTypeSymbol(weather: GeneralForecast): Int {

            val temperatureToShowSunnyDog = 17.0
            val temperatureToShowColdDog = 0.0

            val isNight = weather.symbol.contains("night", ignoreCase = true)
            val isThundering = weather.symbol.contains("thunder", ignoreCase = true) // by only looking at symbol we let Met decide weather they think its thundering now.
            val windSpeed = weather.wind ?: 0.0

            return if (isThundering) R.drawable.dog_thunder
            else if (isNight) R.drawable.dog_sleepy
            else if (windSpeed > 5) R.drawable.dog_wind
            else if (weather.precipitation > 1) R.drawable.dog_rain
            else if (weather.temperature >= temperatureToShowSunnyDog) R.drawable.dog_sunny
            else if (weather.temperature <= temperatureToShowColdDog) R.drawable.dog_cold
            else R.drawable.dog_normal

        }
    }

    /** Contains hashMaps that maps Temperatures, uvIndex and precipitation amount
     * to scores ranging from 1 to 10 */
    object LimitMaps {

        val tempLimitMap: HashMap<List<Double>, Int> =
            hashMapOf(
                listOf(-30.0, -10.1) to 1,
                listOf(30.1, 50.0) to 1,

                listOf(-10.0, -6.0) to 2,
                listOf(29.1, 30.0) to 2,

                listOf(-5.9, -3.0) to 3,
                listOf(28.1, 29.0) to 3,

                listOf(-2.9, 0.9) to 4,
                listOf(25.1, 28.0) to 4,

                listOf(1.0, 2.9) to 5,
                listOf(22.1, 25.0) to 5,

                listOf(3.0, 5.9) to 6,
                listOf(21.1, 22.0) to 6,

                listOf(6.0, 9.9) to 7,
                listOf(19.1, 21.0) to 7,

                listOf(10.0, 12.9) to 8,
                listOf(18.1, 19.0) to 8,

                listOf(13.0, 14.9) to 9,
                listOf(16.6, 18.0) to 9,

                listOf(15.0, 16.5) to 10
            )

        //Based on info from YR: https://hjelp.yr.no/hc/no/articles/115003696813-Hva-er-grensene-for-lett-og-kraftig-nedbør
        val precipitationLimitMap: HashMap<List<Double>, Int> =
            hashMapOf(
                listOf(2.1, 7.0) to 1,
                listOf(1.6, 2.0) to 2,
                listOf(1.1, 1.5) to 3,
                listOf(0.9, 1.0) to 4,
                listOf(0.7, 0.8) to 5,
                listOf(0.5, 0.6) to 6,
                listOf(0.3, 0.4) to 7,
                listOf(0.2, 0.2) to 8,
                listOf(0.1, 0.1) to 9,
                listOf(0.0, 0.0) to 10
            )

        // Based on info from SML: https://sml.snl.no/solfaktor
        val uvLimitMap: HashMap<List<Double>, Int> =
            hashMapOf(
                listOf(8.1, 15.0) to 1,
                listOf(6.6, 8.0) to 2,
                listOf(5.1, 6.5) to 3,
                listOf(4.1, 5.0) to 4,
                listOf(3.7, 4.0) to 5,
                listOf(3.1, 3.6) to 6,
                listOf(2.6, 3.0) to 7,
                listOf(2.1, 2.5) to 8,
                listOf(1.1, 2.0) to 9,
                listOf(0.0, 1.0) to 10
            )

    }


}