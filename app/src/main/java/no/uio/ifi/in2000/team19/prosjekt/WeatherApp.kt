package no.uio.ifi.in2000.team19.prosjekt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Needed for Dagger Hilt dependency injection.

@HiltAndroidApp
class WeatherApp : Application()