package no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast

data class Data(
    val instant: Instant,
    val next_12_hours: Next12Hours,
    val next_1_hours: Next1Hours,
    val next_6_hours: Next6Hours
)