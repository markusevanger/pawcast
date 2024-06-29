package no.uio.ifi.in2000.team19.prosjekt.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import eu.bambooapps.material3.pullrefresh.PullRefreshIndicator
import eu.bambooapps.material3.pullrefresh.pullRefresh
import eu.bambooapps.material3.pullrefresh.rememberPullRefreshState
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.model.WeatherDrawableNameToResourceId
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.WeatherForecast
import no.uio.ifi.in2000.team19.prosjekt.ui.home.BottomInfo
import no.uio.ifi.in2000.team19.prosjekt.ui.temporary.ErrorScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.temporary.LoadingScreen
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Measurements


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(weatherScreenViewModel: WeatherScreenViewModel, navController: NavController) {


    val isRefreshing by remember {
        mutableStateOf(false)
    }
    val state = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { weatherScreenViewModel.loadWeather() })


    val weatherUiState = weatherScreenViewModel.weatherUiState.collectAsStateWithLifecycle().value

    when ( weatherUiState ) {
        is WeatherUiState.Loading -> {
            LoadingScreen()
        }

        is WeatherUiState.Error -> {
            ErrorScreen(
                onReload = { weatherScreenViewModel.loadWeather() },
                reason = weatherUiState.errorReason
            )
        }

        is WeatherUiState.Success -> {


            val location = weatherScreenViewModel.locationUiState.collectAsStateWithLifecycle().value

            val weatherHours = weatherUiState.weatherHours
            val weatherDays = weatherUiState.weatherDays
            val meanHoursForTomorrow = weatherUiState.meanHoursTomorrow
            val meanHoursForDayAfterTomorrow = weatherUiState.meanHoursDayAfterTomorrow


            // Use lazy column here as content is generated from loops, and may vary.
            // In HomeScreen, we use a Column with .verticalScroll modifier, as the content there is "constant" height.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(state)
                    .padding(horizontal = Measurements.HorizontalPadding.measurement)

            ) {

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PullRefreshIndicator(
                            refreshing = isRefreshing, state = state,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                        )
                    }

                }



                item {
                    Column {


                        WeatherNow(weatherHours[0])

                        Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))

                        Row {
                            ElevatedButton(onClick = { navController.navigate("settings") }) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Location"
                                )
                                Text(
                                    text = location.shortName,
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

                    }
                }

                item {
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        TodayForecastCard(allHours = weatherHours.drop(1))
                        NextDaysForecastCard(
                            weatherForecast = weatherDays[0],
                            meanHours = meanHoursForTomorrow
                        )
                        NextDaysForecastCard(
                            weatherForecast = weatherDays[1],
                            meanHours = meanHoursForDayAfterTomorrow
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))
                    BottomInfo(lastUpdated = (weatherHours[0].timeFetched))
                    Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement)) // spacing to not lock items to top of app bar
                }
            }
        }
    }
}


@Composable
fun WeatherNow(weather: GeneralForecast) {




    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        val drawableName = weather.symbol
        if (drawableName in WeatherDrawableNameToResourceId.map) {
            Image(
                painter = painterResource(id = WeatherDrawableNameToResourceId.map[drawableName]!!),
                contentDescription = drawableName,
                modifier = Modifier.size(85.dp)
            )
        }

        Text(
            text = "${weather.temperature}" + stringResource(id = R.string.celcius),
            style = MaterialTheme.typography.displayMedium
        )

        Text(text = stringResource(id = R.string.right_now))
    }

}

@Composable
fun TodayForecastCard(allHours: List<GeneralForecast>) {

    val amountShownExpanded = 12 // amountShownHidden < allHours.length
    val amountShownHidden = 3

    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {

        Column(
            modifier = Modifier
                .padding(
                    vertical = Measurements.WithinSectionVerticalGap.measurement,
                    horizontal = Measurements.HorizontalPadding.measurement
                )
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()

            ) {

                val nextHoursTitle =
                    if (isExpanded) stringResource(R.string.next_hours_button, amountShownExpanded)
                    else stringResource(R.string.next_hours_button, amountShownHidden)

                Text(
                    text = nextHoursTitle,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            val amountOfHoursShown = if (isExpanded) amountShownExpanded else amountShownHidden

            Column(
                modifier = Modifier.animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(Measurements.WithinSectionVerticalGap.measurement)
            ) {
                allHours.take(amountOfHoursShown).map { weather ->
                    SingleHourForecastCard(weather)
                }
            }


            FilledTonalButton(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                onClick = { isExpanded = !isExpanded }
            ) {

                val icon = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
                val text =
                    if (isExpanded) stringResource(R.string.hide) else stringResource(R.string.see_more_hours)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = text, style = MaterialTheme.typography.labelLarge)
                    Icon(imageVector = icon, contentDescription = text)
                }
            }
        }
    }
}

// ============= TOMORROW & DAY AFTER TOMOROW ========================
@Composable
fun NextDaysForecastCard(weatherForecast: WeatherForecast, meanHours: List<WeatherForecast>) {

    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .animateContentSize()
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {

        Column(
            modifier = Modifier
                .padding(
                    vertical = Measurements.WithinSectionVerticalGap.measurement,
                    horizontal = Measurements.HorizontalPadding.measurement
                )
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                val dayWithCapitalizedFirst =
                    weatherForecast.day.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                Text(
                    text = dayWithCapitalizedFirst,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            WholeDayAverageWeatherCard(weatherForecast = weatherForecast)

            AnimatedVisibility(
                visible = isExpanded,
                enter = slideInVertically { -it / 2 } + fadeIn(),
                exit = fadeOut()

            ) {
                Column(
                    modifier = Modifier.animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(Measurements.WithinSectionVerticalGap.measurement)
                ) {
                    meanHours.map { weather ->
                        SixHourMeanForecastCard(weatherForecast = weather)
                    }
                }
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                onClick = { isExpanded = !isExpanded }
            ) {

                val icon = if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown
                val text =
                    if (isExpanded) stringResource(R.string.hide) else stringResource(R.string.see_hourly)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = text, style = MaterialTheme.typography.labelLarge)
                    Icon(imageVector = icon, contentDescription = text)
                }
            }
        }
    }
}

@Composable
fun SingleHourForecastCard(generalForecast: GeneralForecast) {

    val drawableName = generalForecast.symbol

    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text(
                text = generalForecast.hour + ":00",
                style = MaterialTheme.typography.bodyMedium
            )
            if (drawableName in WeatherDrawableNameToResourceId.map){
                Image(
                    painter = painterResource(id = WeatherDrawableNameToResourceId.map[drawableName]!!),
                    contentDescription = drawableName
                )
            }


            Text(
                text = "${generalForecast.temperature} ${stringResource(id = R.string.celcius)}",
                style = MaterialTheme.typography.bodyMedium
            )


            Text(
                text = stringResource(R.string.wind_speed, generalForecast.wind!!), //
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.precipitation, generalForecast.precipitation),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }


}



@Composable
fun WholeDayAverageWeatherCard(weatherForecast: WeatherForecast) {


    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val drawableName = weatherForecast.symbol
            if (drawableName in WeatherDrawableNameToResourceId.map) {
                Image(
                    painter = painterResource(id = WeatherDrawableNameToResourceId.map[drawableName]!!),
                    contentDescription = drawableName,
                    modifier = Modifier.size(85.dp)
                )
            }




            Spacer(modifier = Modifier.padding(10.dp))

            Column {
                Text(

                    text = stringResource(
                        R.string.low_degrees,
                        weatherForecast.lowestTemperature!!
                    ) + stringResource(id = R.string.celcius),

                    style = MaterialTheme.typography.titleSmall
                )



                Text(
                    text = stringResource(
                        R.string.high_degrees,
                        weatherForecast.highestTemperature!!
                    ) + stringResource(id = R.string.celcius),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}


@Composable
fun SixHourMeanForecastCard(weatherForecast: WeatherForecast) {



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Measurements.HorizontalPadding.measurement, vertical = 5.dp),
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text(
                text = stringResource(
                    R.string.timespan_between_hours,
                    weatherForecast.startingTime!!,
                    weatherForecast.endingTime!!
                ),
                style = MaterialTheme.typography.bodyMedium,
            )


            val drawableName = weatherForecast.symbol
            if (drawableName in WeatherDrawableNameToResourceId.map) {
                Image(
                    painter = painterResource(id = WeatherDrawableNameToResourceId.map[drawableName]!!),
                    contentDescription = drawableName,
                    modifier = Modifier.size(85.dp)
                )
            }

            Text(
                text = "${weatherForecast.meanTemperature}" + stringResource(id = R.string.celcius),
                style = MaterialTheme.typography.bodyMedium,
            )


            Text(
                text = stringResource(id = R.string.wind_speed, weatherForecast.wind!!),
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(id = R.string.precipitation, weatherForecast.precipitation!!),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}





