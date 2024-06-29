package no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast

data class Properties(
    val meta: Meta,
    val timeseries: List<Timeseries>
)