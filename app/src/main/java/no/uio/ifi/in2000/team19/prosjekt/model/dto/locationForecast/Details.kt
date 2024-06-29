package no.uio.ifi.in2000.team19.prosjekt.model.dto.locationForecast

data class Details(
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val air_temperature_percentile_10: Double,
    val air_temperature_percentile_90: Double,
    val cloud_area_fraction: Double,
    val cloud_area_fraction_high: Double,
    val cloud_area_fraction_low: Double,
    val cloud_area_fraction_medium: Double,
    val dew_point_temperature: Double,
    val fog_area_fraction: Double,
    val relative_humidity: Double,
    val ultraviolet_index_clear_sky: Double,
    val wind_from_direction: Double,
    val wind_speed: Double,
    val wind_speed_of_gust: Double,
    val wind_speed_percentile_10: Double,
    val wind_speed_percentile_90: Double
)