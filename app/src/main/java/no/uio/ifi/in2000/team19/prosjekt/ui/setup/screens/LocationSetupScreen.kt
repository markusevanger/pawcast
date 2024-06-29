package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationTextField
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchState


@Composable
fun LocationSetupScreen(
    searchLocationViewModel: SearchLocationViewModel,
    onDone: () -> Unit,
) {

    val searchLocationUiState = searchLocationViewModel.uiState.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = stringResource(id = R.string.location)
            )


            Text(
                text = stringResource(R.string.change_location_setup_screen_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.padding(20.dp))



            SearchLocationTextField(

                uiState = searchLocationUiState,

                /** Methods to update state based on user interactions */
                makeSearch = { searchLocationViewModel.searchLocation() },
                updateSearchBoxToRepresentStoredLocation = { searchLocationViewModel.updateSearchBoxToRepresentStoredLocation() },
                updateSearchField = { searchQuery: String ->
                    searchLocationViewModel.updateSearchField(
                        searchQuery
                    )
                },
                selectLocation = { selectedLocation ->
                    searchLocationViewModel.selectSearchLocation(
                        selectedLocation
                    )
                },
                pickTopResult = { searchLocationViewModel.pickTopResult() },
                updateSearchStateToHidden = { searchLocationViewModel.setSearchState(SearchState.Hidden) },
                updateSearchStateToIdle = { searchLocationViewModel.setSearchState(SearchState.Idle) }
            )
        }


        Spacer(modifier = Modifier.padding(10.dp))


        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {

            Text(
                text = stringResource(id = R.string.location_disclaimer),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = searchLocationUiState.isDone,

                onClick = onDone,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = stringResource(id = R.string.next))
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = stringResource(R.string.next_icon_description)
                )
            }

            Spacer(modifier = Modifier.padding(24.dp)) // match button heigth due to Skip button on last screen.
        }
    }


}


