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

    abstract fun weatherResponseDao(): WeatherResponseLocalDao

    abstract fun locationDao(): LocationLocalDao

    abstract fun currentDao(): CurrentLocalDao

    abstract fun forecastDao(): ForecastLocalDao

    abstract fun astroDao(): AstroLocalDao

    abstract fun dayDao(): DayLocalDao

    abstract fun hourDao(): HourLocalDao

    abstract fun cityDao(): CityLocalDao
}