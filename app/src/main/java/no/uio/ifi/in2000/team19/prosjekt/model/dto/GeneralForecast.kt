package no.uio.ifi.in2000.team19.prosjekt.model.dto

import java.time.LocalDateTime


/** Simplifies the `LocationForecast` into the data we care about into
 * a single instance of `GeneralForecast` that represents the forecast for a given `hour` */
data class GeneralForecast(
    val temperature: Double,
    val wind: Double? = null,
    val symbol: String,
    val hour: String,
    val timeFetched: LocalDateTime,
    val precipitation: Double,
    val thunderProbability: Double,
    val uvIndex: Double,
)