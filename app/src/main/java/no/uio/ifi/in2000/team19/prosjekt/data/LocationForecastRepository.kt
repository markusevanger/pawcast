package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.dto.WeatherForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast.LocationForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.dto.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.ForecastTypes
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import javax.inject.Inject


//this repository is responsible for collecting data from the Locationforecast API,
//creating the different forecast-objects with the necessary logic, and
//creating advice-objects from the appropriate categories

class LocationForecastRepository @Inject constructor(
    private val locationForecastDataSource: LocationForecastDataSource,
    private val context: Context,
) {


    //this function collects the API-response from data source, and returns a
    //LocationForecast-object, which is the root of the API-response tree
    private suspend fun fetchLocationForecast(
        latitude: String,
        longitude: String,
    ): LocationForecast? {
        return locationForecastDataSource.getLocationForecast(latitude, longitude)
    }


    //publicly callable functions: getGeneralForecast, getAdvice and getAdviceForecastList

    //this function is to be called from outside the repository, and returns
    //a list of Advice-objects used to display advice cards
    fun getAdvice(generalForecast: ForecastTypes, typeOfDog: UserInfo): List<Advice> {

        val adviceForecast = AdviceFunctions.getAdviceForecastData(generalForecast.general[0])

        val categories = AdviceFunctions.getCategory(adviceForecast, typeOfDog)

        return AdviceFunctions.createAdvice(categories, context)
    }


    //this is necessary for the function that contains graph logic;
    //it uses a list of advice forecasts to generate data points
    fun getAdviceForecastList(listOfGeneralForecasts: ForecastTypes): List<AdviceForecast> {

        val adviceForecasts = mutableListOf<AdviceForecast>()
        val general: List<GeneralForecast> = listOfGeneralForecasts.general
        general.forEach {
            adviceForecasts.add(AdviceFunctions.getAdviceForecastData(it))
        }
        return adviceForecasts
    }


    //this function uses the fetch-function above to generate three different forecast data-objects:
    //GeneralForecast, WeatherForDay and WeatherForecast. These are used to implement different
    //functionalities.
    suspend fun getGeneralForecast(
        latitude: String,
        longitude: String,
        nrDays: Int
    ): ForecastTypes? {

        val locationForecast = fetchLocationForecast(latitude, longitude) ?: return null

        //retrieving time-related data from locationForecast
        val start = locationForecast.properties.timeseries[0].time
        val dateTime = ZonedDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME)
        val startHour = dateTime.toLocalDateTime().truncatedTo(ChronoUnit.HOURS)

        val now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
        val hours = ChronoUnit.HOURS.between(startHour, now)

        //adjusting time parameters for indexing the timeseries property of locationForecast

        //If the time is past midnight, hours will get a negative value
        val adjustedStart = if (hours < 0) 24 + hours.toInt() else hours.toInt()

        //Find ending point of for-loop
        //This equation was suggested from GPT UiO
        val hoursTo23 = (23 - now.hour + 24) % 24 + adjustedStart

        //Used for the other weather functions
        val startOfNextDay = hoursTo23 + 1

        //We always want to show 12 hours ahead, even if the time is 23:00
        val lastHour = (hoursTo23 + 12)

        val genForecastList = mutableListOf<GeneralForecast>()

        //Necessary data is retrieved for each hour, and added to the list of general forecasts
        for (i in adjustedStart..lastHour) {

            val instant = locationForecast.properties.timeseries[i].data.instant.details // shortcut
            val nextOneHours =
                locationForecast.properties.timeseries[i].data.next_1_hours // shortcut

            val temperature = instant.air_temperature
            val wind = instant.wind_speed
            val symbol = nextOneHours.summary.symbol_code
            val time = locationForecast.properties.timeseries[i].time

            val zonedDateTime = ZonedDateTime.parse(time)
            val hourFormatter = DateTimeFormatter.ofPattern("HH")
            val hourAsString = zonedDateTime.format(hourFormatter).toString()

            val timeFetched = LocalDateTime.now() // Store when

            val precipitation = nextOneHours.details.precipitation_amount
            val thunderProbability = nextOneHours.details.probability_of_thunder
            val uvIndex = instant.ultraviolet_index_clear_sky

            genForecastList.add(
                GeneralForecast(
                    temperature,
                    wind,
                    symbol,
                    hourAsString,
                    timeFetched,
                    precipitation,
                    thunderProbability,
                    uvIndex,
                )
            )
        }

        //The two other forecast types are also created, and added to a ForecastTypes-object
        val dayForecastList = getWeatherForecastForDays(locationForecast, nrDays, startOfNextDay)
        val sixHoursIntervalForecastList =
            getWeatherForecastSixHoursInterval(locationForecast, startOfNextDay)

        return ForecastTypes(genForecastList, dayForecastList, sixHoursIntervalForecastList)
    }

    private fun getWeatherForecastForDays(
        locationForecast: LocationForecast,
        nrDays: Int,
        startingHour: Int
    ): List<WeatherForecast> {

        var theHour = startingHour

        var middleOfDay: Int

        val forecastList = mutableListOf<WeatherForecast>()

        for (i in 0 until nrDays) {

            val thisDay = LocalDate.now().plusDays(i.toLong() + 1)
            val dayOfWeekString =
                thisDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            //12:00
            middleOfDay = theHour + 12

            val temperatures = mutableListOf<Double>()

            //Counter so the right hours are included in the repeat
            var hourCounter = theHour

            //Repeat 4 times since we have data for the next 6 hours
            repeat(4) {
                temperatures.add(locationForecast.properties.timeseries[hourCounter].data.next_6_hours.details.air_temperature_max)
                temperatures.add(locationForecast.properties.timeseries[hourCounter].data.next_6_hours.details.air_temperature_min)
                hourCounter += 6
            }

            //Finding the warmest and coldest temperature in 24 hours
            val warmestTemperature = temperatures.max()
            val coldestTemperature = temperatures.min()

            val symbolCode =
                locationForecast.properties.timeseries[middleOfDay].data.next_6_hours.summary.symbol_code

            forecastList.add(
                WeatherForecast(
                    symbolCode,
                    dayOfWeekString,
                    coldestTemperature,
                    warmestTemperature
                )
            )

            theHour += 24
        }

        return forecastList
    }


    private fun getWeatherForecastSixHoursInterval(
        locationForecast: LocationForecast,
        startHour: Int
    ): List<WeatherForecast> {

        val forecastList = mutableListOf<WeatherForecast>()

        var theHour = startHour

        var nextDay = LocalDate.now().plusDays(1)

        //4 intervals per day, so 8 in total
        repeat(8) { i ->

            //Collecting the different times to compare them
            var firstHour: String = locationForecast.properties.timeseries[theHour].time
            var secondHour: String = locationForecast.properties.timeseries[theHour + 1].time
            var lastHour: String = locationForecast.properties.timeseries[theHour + 6].time

            val endHour: String
            var meanWind = 0.0

            //Only want the hour
            firstHour = firstHour.substring(11, 13)
            secondHour = secondHour.substring(11, 13)
            lastHour = lastHour.substring(11, 13)

            val firstHourAsInt = firstHour.toInt()
            val secondHourInt = secondHour.toInt()

            //Check how many hours are between the first and second hour
            //Also if they belong to different days
            val hoursBetween = if (firstHourAsInt <= secondHourInt) {
                secondHourInt - firstHourAsInt
            } else {
                (24 - firstHourAsInt + secondHourInt)
            }

            //Sometimes the difference between two timeseries is 6 hours instead of one
            //Then there is a need to handle the data differently
            if (hoursBetween == 6) {

                meanWind =
                    (locationForecast.properties.timeseries[theHour].data.instant.details.wind_speed +
                            locationForecast.properties.timeseries[theHour + 1].data.instant.details.wind_speed) / 2

                //The end hour is the second hour since there is six hours between them
                endHour = secondHour

            } else {

                //Here the difference is one hour, so we need to check all six
                var hourCounter = theHour

                repeat(6) {
                    meanWind += locationForecast.properties.timeseries[hourCounter].data.instant.details.wind_speed
                    hourCounter += 1
                }

                //The end hour is the last hour
                endHour = lastHour
            }

            //Finding the mean temperature
            val meanTemperature =
                (locationForecast.properties.timeseries[theHour].data.next_6_hours.details.air_temperature_max +
                        locationForecast.properties.timeseries[theHour].data.next_6_hours.details.air_temperature_min) / 2

            val symbolCode =
                locationForecast.properties.timeseries[theHour].data.next_6_hours.summary.symbol_code

            val precipitation =
                locationForecast.properties.timeseries[theHour].data.next_6_hours.details.precipitation_amount

            //Only want one decimal for simplicity
            val roundedTemperature = String.format("%.1f", meanTemperature)
            val roundedWind = String.format("%.1f", meanWind)

            //To update the day
            if (i == 4) {
                nextDay = nextDay.plusDays(1)
            }

            val dayOfWeekString =
                nextDay.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("no", "NO"))

            forecastList.add(
                WeatherForecast(
                    symbolCode,
                    dayOfWeekString,
                    null,
                    null,
                    firstHour,
                    endHour,
                    roundedTemperature,
                    roundedWind,
                    precipitation
                )
            )
            theHour += 6
        }
        return forecastList
    }
}











