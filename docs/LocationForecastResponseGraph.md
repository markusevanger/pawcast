```mermaid

---
title: LocationForecast klassestruktur
---

classDiagram
    
    LocationForecast-->Geometry
    LocationForecast-->Properties
    
    Meta-->Units

    Properties-->Timeseries
    Properties-->Meta

    Timeseries-->Data


    Data-->Instant
    Data-->Next1Hours
    Data-->Next6Hours
    Data-->Next12Hours


    Instant-->Details
    Next1Hours-->DetailsX
    Next6Hours-->DetailsXX
    Next12Hours-->DetailsXXX

    class LocationForecast{
        type: String
        geometry : Geometry
        properties : Properties
    }

    class Geometry{
        type : String
        coordinates : List< Int>
    }

    class Properties{
        meta : Meta
        timeseries : List< Timeseries>
    }

        class Meta{
        updatedAt : String
        units : Units
    }

    class Units{
    
        air_pressure_at_sea_level: String
        air_temperature: String
        air_temperature_max: String
        air_temperature_min: String
        air_temperature_percentile_10: String
        air_temperature_percentile_90: String
        cloud_area_fraction: String
        cloud_area_fraction_high: String
        cloud_area_fraction_low: String
        cloud_area_fraction_medium: String
        dew_point_temperature: String
        fog_area_fraction: String
        precipitation_amount: String
        precipitation_amount_max: String
        precipitation_amount_min: String
        probability_of_precipitation: String
        probability_of_thunder: String
        relative_humidity: String
        ultraviolet_index_clear_sky: String
        wind_from_direction: String
        wind_speed: String
        wind_speed_of_gust: String
        wind_speed_percentile_10: String
        wind_speed_percentile_90: String

    }

    class Timeseries{
        time : String
        data : Data
    }

    class Data{
        instant
        next_12_hours
        next_1_hours
        next_6_hours
    }


    class Instant {
        details : Details
    }
    class Next1Hours{
        details : DetailsX
    }
    class Next6Hours{
        details : DetailsXX
    }
    class Next12Hours{
        details : DetailsXXX
    }


    class Details{
        air_pressure_at_sea_level: Double
        air_temperature: Double
        air_temperature_percentile_10: Double
        air_temperature_percentile_90: Double
        cloud_area_fraction: Double
        cloud_area_fraction_high: Double
        cloud_area_fraction_low: Double
        cloud_area_fraction_medium: Double
        dew_point_temperature: Double
        fog_area_fraction: Double
        relative_humidity: Double
        ultraviolet_index_clear_sky: Double
        wind_from_direction: Double
        wind_speed: Double
        wind_speed_of_gust: Double
        wind_speed_percentile_10: Double
        wind_speed_percentile_90: Double
    }

    class DetailsX{
        probability_of_precipitation: Double
    }

    class DetailsXX{
        precipitation_amount: Double
        precipitation_amount_max: Double
        precipitation_amount_min: Double
        probability_of_precipitation: Double
        probability_of_thunder: Double
    }

    class DetailsXXX{
        air_temperature_max: Double
        air_temperature_min: Double
        precipitation_amount: Double
        precipitation_amount_max: Double
        precipitation_amount_min: Double
        probability_of_precipitation: Double
    }

``````