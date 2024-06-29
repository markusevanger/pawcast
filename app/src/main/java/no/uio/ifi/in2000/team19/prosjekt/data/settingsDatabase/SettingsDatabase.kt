package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Location
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.LocationDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfoDao


@Database(
    entities = [Location::class, UserInfo::class],
    version = 1
)
abstract class SettingsDatabase : RoomDatabase() {
    abstract fun getLocationDao(): LocationDao
    abstract fun getUserInfoDao(): UserInfoDao
}