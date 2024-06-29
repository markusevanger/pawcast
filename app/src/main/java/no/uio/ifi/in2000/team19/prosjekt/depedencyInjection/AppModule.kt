package no.uio.ifi.in2000.team19.prosjekt.depedencyInjection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastDataSource
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.dataStore.DataStoreRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsDatabase
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.LocationDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfoDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton // Tell Dagger-Hilt to create a singleton accessible everywhere in ApplicationCompenent (i.e. everywhere in the application)
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        SettingsDatabase::class.java,
        "settingsDatabase"
    ).build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun provideLocationDao(db: SettingsDatabase) =
        db.getLocationDao() // The reason we can implement a Dao for the database

    @Singleton
    @Provides
    fun provideUserInfoDao(db: SettingsDatabase) = db.getUserInfoDao()

    @Singleton
    @Provides
    fun provideSettingsRepository(
        coordsDao: LocationDao,
        userInfoDao: UserInfoDao
    ): SettingsRepository {
        return SettingsRepository(coordsDao, userInfoDao)
    }

    @Singleton
    @Provides
    fun provideLocationForecastRepository(
        forecastDataSource: LocationForecastDataSource,
        @ApplicationContext context: Context
    ): LocationForecastRepository {
        return LocationForecastRepository(forecastDataSource, context)
    }

    @Singleton
    @Provides
    fun provideLocationForecastDataSource(): LocationForecastDataSource {
        return LocationForecastDataSource()
    }


    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepository(context)
    }
}