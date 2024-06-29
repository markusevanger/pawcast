package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Upsert
    fun insertLocation(location: Location)

    @Delete
    fun deleteLocation(location: Location)

    @Query("SELECT * FROM location")
    fun getLocation(): Flow<Location>?
}