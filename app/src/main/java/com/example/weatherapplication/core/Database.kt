package com.example.weatherapplication.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapplication.data.model.local.*
import com.example.weatherapplication.data.source.local.*

@Database(
    entities = [
        WeatherLocalResponse::class,
        LocalCurrent::class,
        LocalLocation::class,
        LocalForecastDayItem::class,
        LocalAstro::class,
        LocalDay::class,
        LocalHourItem::class,
        CityLocalResponse::class
    ], version = 1
)
abstract class Database : RoomDatabase() {

    abstract fun weatherResponseDao(): WeatherResponseLocalDatasource

    abstract fun locationDao(): LocationLocalDatasource

    abstract fun currentDao(): CurrentLocalDatasource

    abstract fun forecastDao(): ForecastLocalDatasource

    abstract fun astroDao(): AstroLocalDatasource

    abstract fun dayDao(): DayLocalDatasource

    abstract fun hourDao(): HourLocalDatasource

    abstract fun cityDao(): CityLocalDatasource
}