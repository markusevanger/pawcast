package no.uio.ifi.in2000.team19.prosjekt.ui.searchBox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import no.uio.ifi.in2000.team19.prosjekt.R


@Composable
fun SearchLocationTextField(
    uiState: SearchLocationUiState,
    makeSearch: () -> Unit,
    updateSearchBoxToRepresentStoredLocation: () -> Unit,
    updateSearchField: (search: String) -> Unit,
    selectLocation: (selectedLocation: PlaceAutocompleteSuggestion) -> Unit,
    updateSearchStateToIdle: () -> Unit,
    updateSearchStateToHidden: () -> Unit,
    pickTopResult: () -> Unit

) {

    val searchQuery = uiState.searchFieldValue
    val searchState = uiState.searchState
    val isDone = uiState.isDone


    var isTextFieldFocused by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current



    Column(
        modifier = Modifier
    ) {


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isTextFieldFocused = focusState.isFocused
                    if (isTextFieldFocused) {
                        updateSearchStateToIdle()
                    } else {
                        updateSearchStateToHidden()
                        updateSearchBoxToRepresentStoredLocation()
                    }
                },

            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    pickTopResult()
                    updateSearchBoxToRepresentStoredLocation()

                }
            ),

            value = searchQuery,
            singleLine = true,
            onValueChange = { query ->

                updateSearchField(query)
                makeSearch()

            },
            label = { Text(stringResource(R.string.search_box_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_icon_description)
                )
            }
        )

        if (isDone) {
            Text(text = stringResource(R.string.changes_saved_label))
        }



        AnimatedVisibility(

            modifier = Modifier,
            visible = (searchState != SearchState.Hidden)

        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(
                            bottomEnd = 20.dp,
                            bottomStart = 20.dp

                        )
                    )
                    .padding(10.dp)
            ) {

                when (searchState) {
                    is SearchState.Loading -> TextScreenBox { Loading() }
                    is SearchState.NoSuggestions -> TextScreenBox { NoSuggestions() }
                    is SearchState.Error -> TextScreenBox { Error() }
                    is SearchState.Suggestions -> SearchSuggestions(
                        suggestions = searchState.suggestions,
                        selectSearchLocation = { suggestion -> selectLocation(suggestion) },
                        focusManager
                    )

                    is SearchState.Idle -> TextScreenBox { Idle() }
                    is SearchState.Hidden -> { /* Do nothing */
                    }
                }
            }
        }
    }
}


@Composable
fun Loading() {
    CircularProgressIndicator()
    Text(text = stringResource(R.string.search_loading_results))
}

@Composable
fun TextScreenBox(composable: @Composable () -> Unit) {
    Column(
        Modifier
            .height(100.dp)
            .padding(horizontal = 40.dp)
            .fillMaxWidth(),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        composable()
    }
}


@Composable
fun Idle() {
    Text(
        text = stringResource(R.string.search_start_writing),
        textAlign = TextAlign.Center
    )
}


@Composable
fun Error() {
    Text(
        text = stringResource(R.string.search_error_msg),
        textAlign = TextAlign.Center
    ) // Might be bad to ask if user has internet but something else went wrong. Compromise made due to time.
}

@Composable
fun NoSuggestions() {
    Text(text = "🌧️")
    Text(text = "Ingen resultater")
}


@Composable
fun SearchSuggestions(
    suggestions: List<PlaceAutocompleteSuggestion>,
    selectSearchLocation: (suggestion: PlaceAutocompleteSuggestion) -> Unit,
    focusManager: FocusManager
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)

    ) {

        items(suggestions) { suggestion ->
            SearchSuggestion(
                suggestion,
                selectSearchLocation = { selectSearchLocation(suggestion) },
                focusManager = focusManager
            )
        }
    }
}

@Composable
fun SearchSuggestion(
    suggestion: PlaceAutocompleteSuggestion,
    selectSearchLocation: () -> Unit,
    focusManager: FocusManager,
) {


    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = {

            keyboardController?.hide()
            focusManager.clearFocus()
            selectSearchLocation()

        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,

            ) {

            Row {

                Icon(imageVector = Icons.Filled.Place, contentDescription = "icon of Place")

                Spacer(modifier = Modifier.width(10.dp))

                if (suggestion.formattedAddress == null) {
                    Text(text = suggestion.name, maxLines = 1)
                } else {
                    Text(text = suggestion.formattedAddress!!, maxLines = 1)
                }
            }


        }
    }
}
