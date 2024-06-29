package no.uio.ifi.in2000.team19.prosjekt.model.dto

import java.time.LocalDateTime

data class AdviceForecast(
    val temperature: Double,
    val thunderProbability: Double,
    val precipitation: Double,
    val uvIndex: Double,
    val date: LocalDateTime,
    val time: String
)