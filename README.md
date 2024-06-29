# ☀ Team-19
 
- Markus Evanger 🪩
- Synnøve Birkeland 🫧
- Isabelle Røed Lampl 😼
- Mai Anh Duong 🧑‍🎨
- Linn Srongyoo Hjulstad ✨
- Mathias Knudsen 🐌 


## 📦 Hvordan kjøre appen
+ Last ned og installer Android Studio, [se guide her](https://developer.android.com/studio/install)
+ Last ned prosjektmappen via github eller last ned fra innleveringsmappen.
+ Åpne prosjektet i Android Studio [Guide](https://developer.android.com/studio/projects/create-project#ImportAProject).
+ Naviger til `ApiTokens.kt` og følg instruksene for å fylle inn din egen Mapbox token. Husk å også oppdatere den hemmelige mapbox access tokenen i `gradle.properties`  
+ Opprett en emulator eller koble til en Android mobil og kjør appen. [Guide](https://developer.android.com/studio/run/emulator#get-started). 

> Dersom det skjer en feil venligst kjør gradle sync og trykk `build > clean project`



## 📚 Biblioteker
- [Vico Graph](https://github.com/patrykandpatrick/vico), for å vise en graf. Se `HomeScreen` og tilhørende ViewModel
- [MapBox Search SDK](https://docs.mapbox.com/android/search/guides/), for å hente forslag på steder basert på søk. Se `SearchLocationTextField` og tilhørende ViewModel
- [Compose-Markdown](https://github.com/jeziellago/compose-markdown?tab=readme-ov-file) for å vise Advice stylet fritt. Se `AdviceCard` og `AdviceScreen`
- [Material3PullToRefresh](https://github.com/BambooAppsDevTeam/Material3PullToRefresh)
- [Ktor](https://ktor.io/) (HTTP Klient)
- [Room](https://developer.android.com/training/data-storage/room) (Database, for lagring av kordinater og bruker informasjon)
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android) (Dependency Injection)


