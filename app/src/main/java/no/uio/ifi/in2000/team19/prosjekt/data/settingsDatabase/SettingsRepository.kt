package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.Location
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.LocationDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfoDao
import javax.inject.Inject


class SettingsRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val userInfoDao: UserInfoDao
) {


    // CORDS
    suspend fun getLocation(): Flow<Location> {
        val currentLocation = locationDao.getLocation()?.firstOrNull()

        if (currentLocation == null) {
            val defaultLocation =
                Location(
                    id = 0,
                    longitude = "60",
                    latitude = "10",
                    shortName = "",
                    detailedName = ""
                )
            locationDao.insertLocation(defaultLocation)
        }
        return locationDao.getLocation()!!
    }

    fun updateCoords(latitude: String, longitude: String, shortName: String, detailedName: String) {
        val cords = Location(
            id = 0,
            longitude = longitude,
            latitude = latitude,
            shortName = shortName,
            detailedName = detailedName,
        )
        locationDao.insertLocation(cords)
    }

    // USER INFO
    fun getUserInfo(): UserInfo {
        if (userInfoDao.getUserInfo() == null) {
            updateUserInfo(createTemporaryUserinfo())
        } // just to avoid null errors, but we guarantee using dataStores that the user must complete setup if this is null earlier at runtime
        return userInfoDao.getUserInfo()!!
    }

    fun updateUserInfo(userInfo: UserInfo) {
        userInfoDao.upsertUserInfo(userInfo)
    }
}

/**
 *  Returns a UserInfo object populated with fake data. Used for generating temporary UserInfo objects to be later updated by an init method or similar.
 */
fun createTemporaryUserinfo(): UserInfo {
    return UserInfo(
        id = 0,
        userName = "",
        dogName = "",
        isSenior = false,
        isPuppy = false,
        isFlatNosed = false,
        isThin = false,
        isLongHaired = false,
        isShortHaired = false,
        isThinHaired = false,
        isThickHaired = false,
        isLightHaired = false,
        isDarkHaired = false,
        isAdult = false,
        isMediumBody = false,
        isNormalNosed = false,
        isThickBody = false
    )
}