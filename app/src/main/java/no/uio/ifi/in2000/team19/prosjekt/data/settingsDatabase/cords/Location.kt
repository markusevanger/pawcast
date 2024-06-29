package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "location-short-name") var shortName: String,
    @ColumnInfo(name = "location-detailed-name") var detailedName: String,
    @ColumnInfo(name = "longitude") var longitude: String,
    @ColumnInfo(name = "latitude") var latitude: String,
)