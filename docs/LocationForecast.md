

# Location Forecast API

## Intro

>eksempel-kall: https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10+60)&z=123

Dette kallet gir tilbake en ~4500 linjer lang json fil med generell værmelding fra gitte kordinater 

## Hvordan kan vi customize API-kallet?

### Kordinatsystem

https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(LONGTITUDE+LANGTIUDE)&z=HEIGHT



Over har vi fjernet "tallene" fra våre gitte variabler, og disse styrer hvor vi spør API-en om værmeldingen. 


Vi ber om værmeldingen fra et tredimmensjonalt kordinatsystem: 
- LONGITUDE (Breddegrad)
- LATITUDE (Lengdegrad / høydegrad)
- HEIGHT (meter over havet)

eller som i minecraft, X, Y og Z kordinater. bare med hele jordkloden istedenfor. 

Vi kan lett fine kordinatene til hvor enn vi vil med tjenester som denne:  https://www.gps-coordinates.net/ 

IFI har disse kordinatene: 
- LONG: `10.71799373626709`
- LAT: `59.94347381591797`
- HEIGHT: `50` (tilfeldig tall)

Da kan vi danne dette API-kallet: https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10.71799373626709+59.94347381591797)&z=50

Vi kan også droppe høyden fra kallet: 
https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10.71799373626709+59.94347381591797)


### Collections

Vi kan også endre en til variabel i spørringen, som er: `/complete/`


Denne beskriver formatet på spørringen, og vi har 3 alternativer her:

- `/complete` (~4500 linjer)
    - JSON forecast with all values. This will shortly be expanded with probabilities, so that each variable will be repeated for a set of percentiles.

- `/compact` (~3000 linjer)
    - A shorter version with only the most used parameters (if you feel something is missing, please let us know) 

- `/classic`
    - The old XML format, now considered legacy. It matches the output of version 1.9 closely, but includes more time periods. Future new parameters will not be added to this version.


Hentet fra https://api.met.no/weatherapi/locationforecast/2.0/documentation


## Respons 

> 🚨 Se `LocationForcastResponseGraph.md` for en visuell graf av hvordan man navigerer responsen av LocationForecast. 

Etter deserialisering får vi et `LocationForecast` data objekt. 

Den viktigste dataen vi får finner vi med å akksesere:
```kotlin

val forecast = LocationForecast() 
val weater_data = forecast.properties

``````

I `weather_data` finner vi en `List<Timeseries>` som har ~83 entries.

- Index `0` er værmelding __relativt__ til nå.
- Index `10` er værmelding relativt til 10 timer frem i tid. 

```kotlin
val weather_relative_to_now = weather_data[0]
``````

`weather_relative_to_now` har 4 mulige aksesseringer:
+ `Instant` 
+ `next_1_hours`
+ `next_6_hours`
+ `next_12_hours`

Disse fire gir ulik info, hvor `Instant` er relativt til den indexen vi valgte istad. 

`next_1_hours`, `next_6_hours`, `next_12_hours` gir ulik data fra hverandre (_Se LocationForecastResponseGraph_)


Nå kan vi aksesere så mye værdata vi vil 🥰

```kotlin
    val temperature = weather_relative_to_now.Instant.air_temperature
    val wind_speed = weather_realtive_to_now.Instant.wind_speed
```

## Tanker rundt EDR vs vanlige API-kall


La oss sammenligne et vanlig API-kall vs. EDR gitt disse kordinatene:

- LAT: `10`
- LONG: `60`


vanlig: https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=60&lon=10 


EDR: 
https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT(10+60)

Tyngre? Bedre? Mer modulær? Stay tuned 👀
(Burde nok skrive mer om dette med tanke på prosjektinnleveringen)


## Kotlin eksempel: 

### Kalle på API:

```kotlin
suspend fun getLocationForecast(LONGITUDE:String, LATITUDE:String, HEIGHT:String): HttpResponse {
    val client = HttpClient { install(ContentNegotiation) { gson() }} // set up KTOR client
    
    val path = "https://api.met.no/weatherapi/locationforecast/2.0/edr/collections/complete/position?coords=POINT($LONGITUDE+$LATITUDE)&z=$HEIGHT"
    
    val result = client.get(path)
    
    client.close()
    
    return result
}
```

### Deserialisering til LocationForecast objekt:

```kotlin
suspend fun deserializeLocationForecast(response: HttpResponse): LocationForecast {
    return response.body<LocationForecast>()
}
``````
LocationForecast er et `data object` som ligger i `data.objects` sammen med alle andre data objekter tilhørende LocationForecast. Disse er generert med https://json2kt.com/


### Hente temperatur fra LocationForecast:

```kotlin
fun getAirTemperatureFromForecast(forecast: LocationForecast): Double {
    return forecast.properties.timeseries[0].data.instant.details.air_temperature
}
```

> Merk at timeseries er en liste med ~83 Timeseries objekter. I dette eksempelet henter vi bar ut index 0, men senere må vi finne riktig tidspunkt.
