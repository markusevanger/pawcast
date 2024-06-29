package no.uio.ifi.in2000.team19.prosjekt.data

import android.content.Context
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.model.AdviceCategory
import no.uio.ifi.in2000.team19.prosjekt.model.dto.Advice
import no.uio.ifi.in2000.team19.prosjekt.model.dto.AdviceForecast
import no.uio.ifi.in2000.team19.prosjekt.model.dto.GeneralForecast
import java.time.LocalDate
import java.time.LocalDateTime

// functions used by LocationForecastRepository.
// Moved outside for easier testing.


/** Contains functions for creating Advice
 *
 *  Inspired by domain classes for easier testing, but doest use the same "use case" conventions. It also
 *  does not contain any communation with data layer, as domain layers has.
 *
 *  We moved these functions out of LocationForecastRepository to make the code more modular and readable. This was
 *  also really important in making the repository more testable.
 *
 * */
object AdviceFunctions {

    fun getAdviceForecastData(generalForecast: GeneralForecast): AdviceForecast {
        return AdviceForecast(
            generalForecast.temperature,
            generalForecast.thunderProbability,
            generalForecast.precipitation,
            generalForecast.uvIndex,
            generalForecast.timeFetched,
            generalForecast.hour
        )
    }

    //this function collects all necessary categories based on forecast and dog type
    //it uses AdviceForecast because it contains the necessary data
    fun getCategory(adviceForecast: AdviceForecast, typeOfDog: UserInfo): List<AdviceCategory> {

        val categoryList = mutableListOf<AdviceCategory>()

        fun isTemperatureInRange(limits: List<Double>, temp: Double): Boolean {
            return temp in limits[0]..limits[1]
        }

        // Map of category-specific temperatures to check against
        val weatherLimitsMap = mapOf(
            AdviceCategory.COOL to listOf(-5.0, 0.0),
            AdviceCategory.COLD to listOf(-15.0, -5.0),
            AdviceCategory.FREEZING to listOf(-70.0, -15.0),
            AdviceCategory.SALT to listOf(-8.0, 4.0),
            AdviceCategory.WARM to listOf(15.0, 23.0),
            AdviceCategory.VERYWARM to listOf(23.0, 30.0),
            AdviceCategory.HEATWAVE to listOf(30.0, 70.0),
            AdviceCategory.CAR to listOf(18.0, 70.0)
        )


        weatherLimitsMap.forEach { (category, limits) ->
            if (isTemperatureInRange(limits, adviceForecast.temperature)) {
                categoryList.add(category)
            }
        }

        if (typeOfDog.isThin ||
            typeOfDog.isPuppy ||
            typeOfDog.isShortHaired ||
            typeOfDog.isSenior ||
            typeOfDog.isThinHaired
        ) {

            if (AdviceCategory.COOL in categoryList) {
                categoryList.remove(AdviceCategory.COOL)
                categoryList.add(AdviceCategory.COOLOTHER)
            }

            if (AdviceCategory.COLD in categoryList) {
                categoryList.remove(AdviceCategory.COLD)
                categoryList.add(AdviceCategory.COLDOTHER)
            }
        }


        if (typeOfDog.isFlatNosed) {
            if (AdviceCategory.WARM in categoryList) {
                categoryList.remove(AdviceCategory.WARM)
                categoryList.add(AdviceCategory.WARMFLAT)
            }

            if (AdviceCategory.VERYWARM in categoryList) {
                categoryList.remove(AdviceCategory.VERYWARM)
                categoryList.add(AdviceCategory.VERYWARMFLAT)
            }
        }

        if (typeOfDog.isLongHaired)
            if (AdviceCategory.COLD in categoryList) {
                categoryList.remove(AdviceCategory.COLD)
                categoryList.add(AdviceCategory.COLDLONGFUR)
            } else if (AdviceCategory.COLDOTHER in categoryList) {
                categoryList.remove(AdviceCategory.COLDOTHER)
                categoryList.add(AdviceCategory.COLDOTHERLONGFUR)
            }


        if (adviceForecast.uvIndex >= 3 && (
                    typeOfDog.isThinHaired ||
                            typeOfDog.isLightHaired ||
                            typeOfDog.isShortHaired
                    )
        ) {
            categoryList.add(AdviceCategory.SUNBURN)
        }

        val minimumThunderProbabilityForThunderAdvice = 50 // %
        if (adviceForecast.thunderProbability >= minimumThunderProbabilityForThunderAdvice) {
            categoryList.add(AdviceCategory.THUNDER)
        }

        val minimumPrecipitationForRainAlert = 1 // in millimeters
        if (adviceForecast.precipitation >= minimumPrecipitationForRainAlert) {
            categoryList.add(AdviceCategory.RAIN)
        }

        val currentYear = LocalDate.now().year

        val tickSeasonStart = LocalDateTime.of(
            currentYear, 3, 15, 0, 0
        )
        // Year, Month, Day, Hour, Minute (defaults to 00:00)
        val tickSeasonEnd = LocalDateTime.of(currentYear, 11, 15, 0, 0)

        val viperSeasonStart = LocalDateTime.of(currentYear, 2, 28, 0, 0)
        val viperSeasonEnd = LocalDateTime.of(currentYear, 11, 1, 0, 0)

        val newYear = LocalDateTime.of(currentYear, 12, 31, 0, 0)

        val theDate = adviceForecast.date


        if (!theDate.isBefore(tickSeasonStart) && !theDate.isAfter(tickSeasonEnd)) {
            categoryList.add(AdviceCategory.TICK)
        }

        if (!theDate.isBefore(viperSeasonStart) && !theDate.isAfter(viperSeasonEnd)) {
            categoryList.add(AdviceCategory.VIPER)
        }

        if (theDate == newYear) {
            categoryList.add(AdviceCategory.NEWYEAR)
        }


        if (categoryList.size == 0) {
            categoryList.add(AdviceCategory.SAFE)
        }

        return categoryList
    }

    fun createAdvice(categories: List<AdviceCategory>, context: Context): List<Advice> {

        val adviceList = mutableListOf<Advice>()

        if (categories[0] == AdviceCategory.SAFE) {
            val safeArray = context.resources.getStringArray(R.array.SAFE)


            val advice = Advice(safeArray[0], safeArray[1], safeArray[2])
            adviceList.add(advice)
            return adviceList
        }

        categories.forEach { category ->

            var adviceArray: Array<String>? = null

            when (category.toString()) {

                /**
                 * Technical debt: Only return the integer instead of accessing the actual stringArray here
                 *
                 * Not fixed due to time constraints, is one of the reasons the parent HomeScreenViewModel harder to test.
                 *
                 * In a real scenario, if we started testing this alot earlier, we would also likely catch this method as
                 * a bad thing. Therefore ideally this should be moved out, and let ui handle getting the actual string data.
                 *
                 * */
                "COOL" -> adviceArray = context.resources.getStringArray(R.array.COOL)
                "COOLOTHER" -> adviceArray = context.resources.getStringArray(R.array.COOLOTHER)
                "COLD" -> adviceArray = context.resources.getStringArray(R.array.COLD)
                "COLDLONGFUR" -> adviceArray = context.resources.getStringArray(R.array.COLDLONGFUR)
                "COLDOTHER" -> adviceArray = context.resources.getStringArray(R.array.COLDOTHER)
                "COLDOTHERLONGFUR" -> adviceArray = context.resources.getStringArray(R.array.COLDOTHERLONGFUR)

                "FREEZING" -> adviceArray = context.resources.getStringArray(R.array.FREEZING)
                "SALT" -> adviceArray = context.resources.getStringArray(R.array.SALT)
                "WARM" -> adviceArray = context.resources.getStringArray(R.array.WARM)
                "WARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.WARMFLAT)
                "VERYWARM" -> adviceArray = context.resources.getStringArray(R.array.VERYWARM)
                "VERYWARMFLAT" -> adviceArray = context.resources.getStringArray(R.array.VERYWARMFLAT)

                "HEATWAVE" -> adviceArray = context.resources.getStringArray(R.array.HEATWAVE)
                "RAIN" -> adviceArray = context.resources.getStringArray(R.array.RAIN)
                "THUNDER" -> adviceArray = context.resources.getStringArray(R.array.THUNDER)
                "SUNBURN" -> adviceArray = context.resources.getStringArray(R.array.SUNBURN)
                "TICK" -> adviceArray = context.resources.getStringArray(R.array.TICK)
                "VIPER" -> adviceArray = context.resources.getStringArray(R.array.VIPER)
                "CAR" -> adviceArray = context.resources.getStringArray(R.array.CAR)

                "NEWYEAR" -> adviceArray = context.resources.getStringArray(R.array.NEWYEAR)


            }

            var counter = 0
            if (adviceArray != null) {
                while (counter < adviceArray.size) {

                    val title = adviceArray[counter]
                    val description = adviceArray[counter + 1]
                    val shortAdvice = adviceArray[counter + 2]

                    val advice = Advice(title, description, shortAdvice)
                    adviceList.add(advice)

                    counter += 3

                }
            }
        }

        return adviceList
    }
}