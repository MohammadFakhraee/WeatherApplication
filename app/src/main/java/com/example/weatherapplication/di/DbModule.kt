package com.example.weatherapplication.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapplication.core.Database
import com.example.weatherapplication.data.source.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): Database = Room.databaseBuilder(context, Database::class.java, "MyDb").build()

    @Provides
    @Singleton
    fun providesLocalWeatherResponseDao(database: Database): WeatherResponseLocalDatasource = database.weatherResponseDao()

    @Provides
    @Singleton
    fun providesLocalLocationDao(database: Database): LocationLocalDatasource = database.locationDao()

    @Provides
    @Singleton
    fun providesLocalCurrentDao(database: Database): CurrentLocalDatasource = database.currentDao()

    @Provides
    @Singleton
    fun providesLocalForecastDao(database: Database): ForecastLocalDatasource = database.forecastDao()

    @Provides
    @Singleton
    fun providesLocalAstroDao(database: Database): AstroLocalDatasource = database.astroDao()

    @Provides
    @Singleton
    fun providesLocalDayDao(database: Database): DayLocalDatasource = database.dayDao()

    @Provides
    @Singleton
    fun providesLocalHourDao(database: Database): HourLocalDatasource = database.hourDao()

    @Provides
    @Singleton
    fun providesCityLocalDao(database: Database): CityLocalDatasource = database.cityDao()
}