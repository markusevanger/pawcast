package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Location
import no.uio.ifi.in2000.team19.prosjekt.model.ErrorReasons
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.WeatherForecast
import java.io.IOException
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

//this viewmodel is responsible for handling the weather ui state, and updating the coordinates
//of which the weather is collected from

//the weather ui state is used by both weatherscreen and homescreen in order to display
//the weatherforecast in real-time

//this ui-state interface holds the weather variable containing a ForecastType-object
//this object contains three forecast objects that have different uses
sealed interface WeatherUiState {
    data class Success(
        val weatherHours: List<GeneralForecast>,
        val weatherDays: List<WeatherForecast>,
        val meanHoursTomorrow: List<WeatherForecast>,
        val meanHoursDayAfterTomorrow: List<WeatherForecast>
    ) : WeatherUiState

    data object Loading : WeatherUiState
    data class Error(val errorReason: ErrorReasons) : WeatherUiState
}

@HiltViewModel
class WeatherScreenViewModel @Inject constructor(
    private val locationForecastRepository: LocationForecastRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {


    private val _weatherUiState: MutableStateFlow<WeatherUiState> =
        MutableStateFlow(WeatherUiState.Loading)
    var weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    private var _locationUiState: MutableStateFlow<Location> =
        MutableStateFlow(Location(0, "default", "default", "69", "69"))
    var locationUiState: StateFlow<Location> = _locationUiState.asStateFlow()


    init {

        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.getLocation().collect { location ->
                _locationUiState.value = location
                loadWeather()
            }

        }
    }

    //this function is public in order to reload the weather and location when needed
    fun loadWeather() {

        val location = _locationUiState.value
        _weatherUiState.value = WeatherUiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherForecast = locationForecastRepository.getGeneralForecast(
                    location.latitude,
                    location.longitude,
                    2
                ) ?: throw IOException() // throws an error to be caught by try... catch..

                val weatherHours = weatherForecast.general
                val weatherDays = weatherForecast.day
                val weatherMean = weatherForecast.hours

                val differentDays = weatherDays.map { it.day }.distinct()

                val meanHoursForTomorrow = weatherMean.filter { it.day == differentDays[0] }
                val meanHoursForDayAfterTomorrow = weatherMean.filter { it.day == differentDays[1] }

                _weatherUiState.value = WeatherUiState.Success(
                    weatherHours,
                    weatherDays,
                    meanHoursForTomorrow,
                    meanHoursForDayAfterTomorrow
                )

                // See similar try-catch in HomeScreenViewModel for explanation of connection handling.
            } catch (e: IOException) {
                _weatherUiState.value = WeatherUiState.Error(ErrorReasons.INTERRUPTION)
            } catch (e: UnresolvedAddressException) {
                _weatherUiState.value = WeatherUiState.Error(ErrorReasons.INTERNET)
            } catch (e: Exception) {
                _weatherUiState.value = WeatherUiState.Error(ErrorReasons.UNKNOWN)
            }

        }
    }

    fun checkIfUiStateIsError(): Boolean {
        return _weatherUiState.value is WeatherUiState.Error
    }
}
