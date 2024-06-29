package no.uio.ifi.in2000.team19.prosjekt

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfo
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfoDao
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserinfoDatabaseTest {
    private lateinit var dao: UserInfoDao
    private lateinit var db: SettingsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, SettingsDatabase::class.java
        ).build()
        dao = db.getUserInfoDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun checkDatabaseContent() {
        val expectedUserInfo = UserInfo(
            0,
            "Coco",
            "Isabel",
            true,
            isAdult = true,
            isPuppy = true,
            isFlatNosed = true,
            isNormalNosed = false,
            isThin = false,
            isMediumBody = false,
            isThickBody = true,
            isLongHaired = true,
            isShortHaired = false,
            isThinHaired = false,
            isThickHaired = false,
            isLightHaired = false,
            isDarkHaired = false
        )
        dao.upsertUserInfo(expectedUserInfo)

        val userInfo = dao.getUserInfo()
        Assert.assertEquals(expectedUserInfo, userInfo)

        val fakeInfo = UserInfo(
            0,
            "Isabel",
            "Coco",
            true,
            true,
            true,
            true,
            false,
            false,
            false,
            true,
            true,
            true,
            false,
            false,
            false,
            false
        )
        dao.deleteUserInfo(fakeInfo)

        Assert.assertEquals(expectedUserInfo, userInfo)

        dao.deleteUserInfo(expectedUserInfo)

        Assert.assertEquals(null, dao.getUserInfo())
    }
}