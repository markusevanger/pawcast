

# MapBox API

```
OPPDATERING: Vi endte aldri opp med å implementere et kart i appen vår, så deler av dette ble ikke brukt. Setup av tokens gjelder enda MapBox Search SDK.

```


MapBox er nok den letteste kart API vi kan implementere.
 

## Setup


>>>⚠️ !! Denne guiden tar utgangspunkt i [<u>denne guiden</u> ](https://docs.mapbox.com/android/maps/guides/install) !! ⚠️:



Dette er allerede gjort i prosjektet men dokumenterer her hva som er gjort: 


### Token setup

Laget public og private API tokens som er nødvendige for å kjøre appen. 

- Public token er lagret i prosjekt pathen: `app/src/main/res/values/developer_config.xml`
    - Her finner du en [_string resource_](https://developer.android.com/guide/topics/resources/string-resource#String), som lagrer denne i prosjektet vårt. Idk hvordan men MapBox finner automatisk denne når vi kjører programmet

- Private token er lagret i `gradle.properties` med navnet `MAPBOX_DOWNLOADS_TOKEN`

Disse blir automatisk brukt av MapBox når vi kaller på APIene

### Dependencies

I `settings.gradle.kts` har vi lagt til følgende:

```kts
    repositories {
        google()
        mavenCentral()
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            // Do not change the username below. It should always be "mapbox" (not your username).
            credentials.username = "mapbox"
            // Use the secret token stored in gradle.properties as the password
            credentials.password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            authentication.create<BasicAuthentication>("basic")
        }
    }

```

Dette kobler sammen API tokensene våre og gir oss tilgang til MapBox ressurser ...

I `build.gradle.kts` har vi lagt til to dependencies:
```kts
    implementation("com.mapbox.maps:android:11.2.0") // nødvendig
    implementation("com.mapbox.extension:maps-compose:11.2.0") // bruke jetpack compose "versjonen" 
````



## Hvordan bruke MapBox?

### Eksempel Composable

```kotlin
@OptIn(MapboxExperimental::class)
@Composable
fun MapBoxSimpleExample(){
    MapboxMap(
        Modifier
            .fillMaxWidth()
            .height(10.dp),

        mapViewportState = MapViewportState().apply {
            setCameraOptions {
                zoom(10.0)
                center(Point.fromLngLat(10.0, 60.0))
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}
```

## Linker
- Generelle docs (kan navigere til alt herfra): https://docs.mapbox.com/android/maps/guides/
- Installasjon (90% er hentet herfra): https://docs.mapbox.com/android/maps/guides/install

