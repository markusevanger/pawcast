package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserInfoDao {

    @Upsert
    fun upsertUserInfo(userInfo: UserInfo)

    @Delete
    fun deleteUserInfo(userInfo: UserInfo)

    @Query("SELECT * FROM userInfo")
    fun getUserInfo(): UserInfo?
}