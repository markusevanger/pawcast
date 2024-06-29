package no.uio.ifi.in2000.team19.prosjekt.ui.settings


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationTextField
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchState
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.TipBox
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Measurements


@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    searchLocationViewModel: SearchLocationViewModel,
    navController: NavController,
    innerPadding: PaddingValues
) {


    Column(
        modifier = Modifier
            .padding(
                start = Measurements.HorizontalPadding.measurement,
                end = Measurements.HorizontalPadding.measurement,
                top = innerPadding.calculateTopPadding() + 30.dp
            ) // Global horizontal padding for all settings items.
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings)
            )
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.displaySmall
            )
        }



        CategoryDivider(text = stringResource(id = R.string.location))
        Column(
            modifier = Modifier.padding(top = 10.dp)
        ) {

            val searchLocationUiState = searchLocationViewModel.uiState.collectAsStateWithLifecycle().value

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
            Spacer(modifier = Modifier.padding(5.dp))
            Text(
                text = stringResource(R.string.location_disclaimer),
                style = MaterialTheme.typography.labelMedium
            )
        }


        CategoryDivider(text = stringResource(R.string.your_dog_title))

        val buttonModifiers = Modifier.weight(1f)
        val labelModifier = Modifier.weight(1f)
        val labelStyle = MaterialTheme.typography.titleMedium

        val rowAlignment = Alignment.CenterVertically

        val userInfo = uiState.userInfo



        Row(verticalAlignment = rowAlignment) {

            Text(
                text = stringResource(R.string.name_category_label),
                modifier = labelModifier,
                style = labelStyle
            )

            FilledTonalButton(
                onClick = { navController.navigate("setup/only_1") },
                modifier = buttonModifiers
            ) {
                val nameButtonText =
                    if (userInfo.userName.isNotBlank() && userInfo.dogName.isNotBlank()) "${userInfo.userName} og ${userInfo.dogName}"
                    else if (userInfo.userName.isNotBlank()) userInfo.userName
                    else if (userInfo.dogName.isNotBlank()) userInfo.dogName
                    else stringResource(R.string.no_name_defined)

                Text(text = nameButtonText)
            }
        }


        Row(verticalAlignment = rowAlignment) {
            Text(
                text = stringResource(R.string.age_category_label),
                modifier = labelModifier,
                style = labelStyle
            )
            FilledTonalButton(
                onClick = { navController.navigate("setup/only_3") },
                modifier = buttonModifiers
            ) {
                val ageButtonText =
                    if (userInfo.isPuppy) stringResource(id = R.string.puppy)
                    else if (userInfo.isSenior) stringResource(id = R.string.senior)
                    else if (userInfo.isAdult) stringResource(id = R.string.adult)
                    else stringResource(R.string.not_defined)
                Text(text = ageButtonText)
            }
        }

        Row(verticalAlignment = rowAlignment) {
            Text(
                text = stringResource(R.string.nose_category_label),
                modifier = labelModifier,
                style = labelStyle
            )
            FilledTonalButton(
                onClick = { navController.navigate("setup/only_4") },
                modifier = buttonModifiers
            ) {
                val noseButtonText =
                    if (userInfo.isFlatNosed) stringResource(id = R.string.flat_nose)
                    else if (userInfo.isNormalNosed) stringResource(id = R.string.normal_nose)
                    else stringResource(id = R.string.normal_nose)
                Text(text = noseButtonText)
            }

        }

        Row(verticalAlignment = rowAlignment) {
            Text(
                text = stringResource(R.string.body_category_label),
                modifier = labelModifier,
                style = labelStyle
            )
            FilledTonalButton(
                onClick = { navController.navigate("setup/only_5") },
                modifier = buttonModifiers
            ) {
                val bodyButtonText =
                    if (userInfo.isThin) stringResource(id = R.string.skinnyBody)
                    else if (userInfo.isMediumBody) stringResource(id = R.string.mediumBody)
                    else if (userInfo.isThickBody) stringResource(id = R.string.fatBody)
                    else stringResource(id = R.string.not_defined)
                Text(text = bodyButtonText)
            }
        }


        Row(verticalAlignment = rowAlignment) {
            Text(
                text = stringResource(R.string.fur_category_label),
                modifier = labelModifier,
                style = labelStyle
            )
            FilledTonalButton(
                onClick = { navController.navigate("setup/only_6") },
                modifier = buttonModifiers
            ) {

                var amountOfFurChoices = 0
                if (userInfo.isThinHaired) amountOfFurChoices += 1
                if (userInfo.isThickHaired) amountOfFurChoices += 1
                if (userInfo.isDarkHaired) amountOfFurChoices += 1
                if (userInfo.isLightHaired) amountOfFurChoices += 1
                if (userInfo.isLongHaired) amountOfFurChoices += 1
                if (userInfo.isShortHaired) amountOfFurChoices += 1

                Text(text = stringResource(R.string.amount_of_fur_chosen, amountOfFurChoices))
            }
        }

        Spacer(modifier = Modifier.padding(Measurements.WithinSectionHorizontalGap.measurement))
        TipBox(tipText = "For at anbefalingene vi gir deg skal være best mulig, burde du holde disse kategoriene oppdatert")
        Spacer(modifier = Modifier.padding(Measurements.WithinSectionHorizontalGap.measurement))
    }
}


@Composable
fun CategoryDivider(text: String) {

    Column(
        modifier = Modifier.padding(top = 40.dp)
    ) {
        Text(text = text)
        HorizontalDivider()
    }

}