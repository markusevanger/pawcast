package no.uio.ifi.in2000.team19.prosjekt.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.uio.ifi.in2000.team19.prosjekt.ui.extendedAdvice.AdviceScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenManager
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.searchBox.SearchLocationViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.settings.SettingsScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupManager
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.weather.WeatherScreenViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScreenManager(
    viewModel: ScreenManagerViewModel,
) {

    val navController = rememberNavController()
    val navBarItems = createBottomNavbarItems()

    val navBarSelectedItemIndex = viewModel.navBarSelectedIndex.collectAsStateWithLifecycle().value
    val startDestination = viewModel.startDestination.collectAsStateWithLifecycle().value

    Scaffold(
        bottomBar = {
            if (startDestination == "home" || startDestination == "weather" || startDestination == "settings") {
                NavigationBar {
                    navBarItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = (index == navBarSelectedItemIndex),
                            onClick = {
                                viewModel.updateNavBarSelectedIndex(index)
                                navController.navigate(item.title)
                            },
                            icon = {
                                Icon(
                                    imageVector =
                                    if (index == navBarSelectedItemIndex) item.selectedIcon
                                    else item.unselectedIcon,

                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding(),
            )
        ) {
            //Sjekk kun for når man åpner appen
            NavHost(
                navController = navController,
                startDestination = startDestination,
                route = "parent"

            ) {
                composable("home") { backStackEntry ->
                    viewModel.updateNavBarSelectedIndex(0) // added to make sure index is correct, as when user re-enters home screen after going through setup from settings, the navbar would still remember its last position.

                    // hiltViewModel creates new viewmodel model if there is none and stores it scoped to the navigation graph. https://developer.android.com/reference/kotlin/androidx/hilt/navigation/compose/package-summary
                    val parentEntry =
                        remember(backStackEntry) { navController.getBackStackEntry("parent") }
                    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel(parentEntry)

                    if (homeScreenViewModel.checkIfUiStateIsError()) {
                        homeScreenViewModel.loadWeatherForecast()
                    }

                    HomeScreenManager(
                        viewModel = homeScreenViewModel,
                        navController = navController
                    )
                }

                composable("weather") { backStackEntry ->

                    viewModel.updateNavBarSelectedIndex(1)

                    val parentEntry =
                        remember(backStackEntry) { navController.getBackStackEntry("parent") }
                    val weatherScreenViewModel: WeatherScreenViewModel = hiltViewModel(parentEntry)

                    if (weatherScreenViewModel.checkIfUiStateIsError()) {
                        weatherScreenViewModel.loadWeather()
                    }

                    WeatherScreen(
                        weatherScreenViewModel = weatherScreenViewModel,
                        navController = navController,
                    )
                }


                composable("settings") { backStackEntry ->

                    viewModel.updateNavBarSelectedIndex(2)


                    val parentEntry =
                        remember(backStackEntry) { navController.getBackStackEntry("parent") }

                    val settingsScreenViewModel: SettingsScreenViewModel =
                        hiltViewModel(parentEntry)
                    val searchLocationViewModel: SearchLocationViewModel =
                        hiltViewModel(parentEntry)

                    settingsScreenViewModel.fetchUserInfo() // always keep these settings updated when user navigates to this screen.
                    val settingUiState =
                        settingsScreenViewModel.uiState.collectAsStateWithLifecycle().value

                    SettingsScreen(
                        uiState = settingUiState,
                        searchLocationViewModel = searchLocationViewModel,
                        navController = navController,
                        innerPadding = innerPadding
                    )
                }



                composable(
                    route = "setup/{STAGE}",
                    arguments = listOf(navArgument("STAGE") { defaultValue = "0" })


                ) { backStackEntry ->

                    val parentEntry =
                        remember(backStackEntry) { navController.getBackStackEntry("parent") }
                    val setupScreenViewModel: SetupScreenViewModel = hiltViewModel(parentEntry)
                    val searchLocationViewModel: SearchLocationViewModel =
                        hiltViewModel(parentEntry)


                    setupScreenViewModel.updateSelectedIndexesBasedOnUserData()

                    val id = backStackEntry.arguments?.getString("STAGE")
                        ?: "0" // 0 to force it to start if wrong parameter is supplied. Elvis operator needs to stay for when start destination supplies the argument
                    SetupManager(
                        id = id,
                        navController = navController,
                        viewModel = setupScreenViewModel,
                        searchLocationViewModel = searchLocationViewModel
                    )
                }

                composable("advice/{id}") { backStackEntry ->

                    val parentEntry =
                        remember(backStackEntry) { navController.getBackStackEntry("parent") }
                    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel(parentEntry)

                    val id = backStackEntry.arguments?.getString("id") ?: "0"

                    AdviceScreen(
                        navController = navController,
                        adviceId = id.toInt(),
                        viewModel = homeScreenViewModel
                    )
                }
            }
        }
    }
}


data class BottomNavBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

fun createBottomNavbarItems(): List<BottomNavBarItem> {


    return listOf(

        BottomNavBarItem(
            title = "home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),

        BottomNavBarItem(
            title = "weather",
            selectedIcon = Icons.Filled.Cloud,
            unselectedIcon = Icons.Outlined.Cloud
        ),

        BottomNavBarItem(
            title = "settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        ),

        )
}