package no.uio.ifi.in2000.team19.prosjekt

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Location
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.LocationDao
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LocationDatabaseTest {
    private lateinit var dao: LocationDao
    private lateinit var db: SettingsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, SettingsDatabase::class.java
        ).build()
        dao = db.getLocationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun checkDatabaseContent() = runTest {
        val expectedLocation = Location(0, "navn", "navn2", "60", "10")
        dao.insertLocation(expectedLocation)

        val location = getLocationFromFlow()
        assertEquals(expectedLocation, location)

        val fakeLocation = Location(0, "hallo", "ok", "2", "5")
        dao.deleteLocation(fakeLocation)

        assertEquals(expectedLocation, location)

        dao.deleteLocation(expectedLocation)

        assertEquals(null, getLocationFromFlow())
    }

    private suspend fun getLocationFromFlow(): Location? = dao.getLocation()?.firstOrNull()

}