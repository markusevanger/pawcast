package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.AgeSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.BodySetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.FurSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.LocationSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.NamesSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.NoseSetupScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens.WelcomeScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Measurements


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupManager(
    viewModel: SetupScreenViewModel,
    searchLocationViewModel: SearchLocationViewModel,
    id: String,
    navController: NavHostController
) {


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (!id.contains("only") && id != "0") { // hide progress indicator if user has navigated to Setup from settings (using "only_id")
                        val amountOfSetupPages = "7"
                        Text(
                            text = stringResource(
                                R.string.setup_stage_count,
                                id.toInt() + 1,
                                amountOfSetupPages
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                },
                navigationIcon = {
                    if (id != "0") {
                        IconButton(onClick = {
                            // This double if check needs to stay as there is some bug in "hiding" the navigation Icon that does hide the icon,
                            // but allows the user to run the function below after its hidden by clicking the area it was.
                            if (id != "0") { // <--- keep this.
                                navController.popBackStack()
                            }
                        }
                        ) {
                            Icon( //Er ikke Material Design 3
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = stringResource(id = R.string.GoBack_icon_description)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = Measurements.HorizontalPadding.measurement,
                    end = Measurements.HorizontalPadding.measurement,

                    )
        ) {


            when (id) {
                // For going to next step after finnishing.
                "0" -> WelcomeScreen(
                    onDone = { navController.navigate("setup/1") },
                    onSkip = {
                        navController.navigate("setup/skip_and_only_setup_location")
                    }
                )

                // when user wants to skip from Welcome screen
                "skip_and_only_setup_location" -> LocationSetupScreen(searchLocationViewModel) {
                    viewModel.handleUserSkip()
                    navController.navigate("settings")
                }


                "1" -> NamesSetupScreen(viewModel) { navController.navigate("setup/2") }
                "2" -> LocationSetupScreen(searchLocationViewModel) { navController.navigate("setup/3") }
                "3" -> AgeSetupScreen(viewModel) { navController.navigate("setup/4") }
                "4" -> NoseSetupScreen(viewModel) { navController.navigate("setup/5") }
                "5" -> BodySetupScreen(viewModel) { navController.navigate("setup/6") }
                "6" -> FurSetupScreen(viewModel) {
                    viewModel.saveUserInfo()
                    viewModel.saveSetupState(isCompleted = true)
                    navController.popBackStack()
                    navController.navigate("home")
                }

                // When editing from settings screen.
                "only_1" -> NamesSetupScreen(viewModel) {
                    viewModel.saveUserInfo()
                    navController.navigate("settings")
                }

                "only_2" -> LocationSetupScreen(searchLocationViewModel) {
                    viewModel.saveUserInfo()
                    navController.navigate("settings")
                }

                "only_3" -> AgeSetupScreen(viewModel) {
                    viewModel.saveUserInfo()
                    navController.navigate("settings")
                }

                "only_4" -> NoseSetupScreen(viewModel) {
                    viewModel.saveUserInfo()
                    navController.navigate("settings")
                }

                "only_5" -> BodySetupScreen(viewModel) {
                    viewModel.saveUserInfo()
                    navController.navigate("settings")
                }

                "only_6" -> FurSetupScreen(viewModel) {
                    viewModel.saveUserInfo()
                    navController.navigate("settings")
                }
            }
        }
    }
}

@Composable
fun TipBox(tipText: String) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Row(
            modifier = Modifier
                .padding(Measurements.HorizontalPadding.measurement)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = stringResource(R.string.lightbulb_icon_description)
            )
            Spacer(modifier = Modifier.padding(Measurements.WithinSectionHorizontalGap.measurement))
            Text(text = tipText, style = MaterialTheme.typography.bodyMedium)
        }
    }
}















