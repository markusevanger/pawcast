package no.uio.ifi.in2000.team19.prosjekt.ui.searchBox

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team19.prosjekt.data.ApiKeys
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import javax.inject.Inject


sealed class SearchState {
    data object Hidden : SearchState()
    data object Idle : SearchState()
    data object Loading : SearchState()
    data object NoSuggestions : SearchState()
    data class Suggestions(val suggestions: List<PlaceAutocompleteSuggestion>) : SearchState()
    data object Error : SearchState()

}

data class SearchLocationUiState(
    var isDone: Boolean,
    var searchFieldValue: String,
    var searchState: SearchState
)


@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {


    private val _uiState: MutableStateFlow<SearchLocationUiState> = MutableStateFlow(
        SearchLocationUiState(
            isDone = false,
            searchFieldValue = "",
            searchState = SearchState.Hidden
        )
    )
    val uiState: StateFlow<SearchLocationUiState> = _uiState.asStateFlow()

    private val placeAutocomplete = PlaceAutocomplete.create(ApiKeys.MAPBOX_PUBLIC_ACCESS_TOKEN)

    // Set Text in TextField to match stored value
    init {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.getLocation().collect { storedLocation ->
                updateSearchField(storedLocation.detailedName)
                if (storedLocation.detailedName != "") { // if database is already populated from database.
                    setIsDone(true)
                }
            }
        }
    }

    fun updateSearchField(search: String) {
        _uiState.value = _uiState.value.copy(searchFieldValue = search)
        Log.d("debug", "Updating search: ${_uiState.value.searchFieldValue}")
    }

    private var debounceJob: Job? = null
    private var topSuggestion = mutableStateOf<PlaceAutocompleteSuggestion?>(null)

    /** Makes search using current value from SearchField.
    🚨 NB: Is called everytime the user makes a change to the text field. Therefore contains debouncing. */
    fun searchLocation() {

        setSearchState(SearchState.Loading)

        // Debounce is about waiting 200ms to make sure the user has stopped typing. Helps with making less API calls.
        debounceJob?.cancel() // Cancel last job (if it exists)
        val debounceDelay = 200 // milliseconds


        // Assigning this coroutine to a variable allows us to use methods like .cancel()
        debounceJob = viewModelScope.launch(Dispatchers.IO) {

            try {

                delay(timeMillis = debounceDelay.toLong()) // Debounce / wait 200 ms.

                // Debounce allows us not call the API for every letter typed, only when the user typed then paused for 200 ms

                if (isActive) {
                    val response = placeAutocomplete.suggestions(_uiState.value.searchFieldValue)

                    // ⛔ if API returns error or response is null
                    if (response.value == null || response.isError) {
                        setSearchState(SearchState.Error)
                    }

                    // ✅ if search is Successful
                    else if (response.value!!.isNotEmpty()) {
                        topSuggestion.value =
                            response.value!![0] // Save top result in case user just presses done.
                        setSearchState(SearchState.Suggestions(response.value!!))
                    }

                    // 👎 There was no results.
                    else {
                        setSearchState(SearchState.NoSuggestions)
                    }
                }
            } catch (e: CancellationException) {
                Log.d("Search", "Debounce cancelled. New Job started.")
            }
        }
    }

    /** Tell API that we have selected this suggestions. API then returns more detailed info about Place. */
    fun selectSearchLocation(selectedSuggestion: PlaceAutocompleteSuggestion) {

        setSearchState(SearchState.Hidden)

        viewModelScope.launch(Dispatchers.IO) {
            val response = placeAutocomplete.select(selectedSuggestion)
            if (response.isValue) {
                settingsRepository.updateCoords(
                    latitude = response.value!!.coordinate.latitude().toString(),
                    longitude = response.value!!.coordinate.longitude().toString(),
                    shortName = response.value!!.name,
                    detailedName = response.value!!.address!!.formattedAddress
                        ?: response.value!!.name, // some addresses don't have a detailedName.
                )

                updateSearchBoxToRepresentStoredLocation()
                setIsDone(true)

            } else {
                setSearchState(SearchState.Error)
            }

        }

    }

    fun updateSearchBoxToRepresentStoredLocation() {

        viewModelScope.launch(Dispatchers.IO) {
            val cords = settingsRepository.getLocation()
            cords.collect {
                _uiState.value.searchFieldValue = it.shortName
            }
        }
    }

    fun setSearchState(state: SearchState) {
        if (state is SearchState.Idle) setIsDone(false)
        _uiState.value = _uiState.value.copy(searchState = state)
    }

    fun setIsDone(value: Boolean) {
        _uiState.value = _uiState.value.copy(isDone = value)
    }

    /** This method picks the current top result in the suggestions. Used if the user just presses done on their keyboard */
    fun pickTopResult() {
        if (topSuggestion.value != null) {
            selectSearchLocation(topSuggestion.value!!)
        }
    }
}

