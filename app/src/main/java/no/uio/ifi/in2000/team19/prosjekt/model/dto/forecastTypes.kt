package no.uio.ifi.in2000.team19.prosjekt.model.dto

data class ForecastTypes(
    val general: List<GeneralForecast>,
    val day: List<WeatherForecast>,
    val hours: List<WeatherForecast>
)
