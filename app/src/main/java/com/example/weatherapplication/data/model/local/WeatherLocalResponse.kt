package com.example.weatherapplication.data.model.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Local response when requesting for weather of city or specific location
 */
@Entity(tableName = "weather")
data class WeatherLocalResponse(
    @Ignore val current: LocalCurrent = LocalCurrent(),
    @Ignore val location: LocalLocation = LocalLocation(),
    @Ignore val forecastDay: List<LocalForecastDayItem> = arrayListOf(),
    @PrimaryKey(autoGenerate = true) var id: Long = 0
)

@Entity(
    tableName = "current_weather",
    foreignKeys = [ForeignKey(
        entity = WeatherLocalResponse::class,
        parentColumns = ["id"],
        childColumns = ["weatherId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalCurrent(
    val feelsLikeC: Int = 0,
    val uv: Int = 0,
    val tempC: Int = 0,
    val pressureMb: Int = 0,
    val gustKph: Int = 0,
    val cloud: Int = 0,
    val windKph: Int = 0,
    val conditionCode: Int = 0,
    val conditionIcon: String = "",
    val conditionText: String = "",
    val visKm: Int = 0,
    val humidity: Int = 0,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var weatherId: Long = 0
)

@Entity(
    tableName = "current_location",
    foreignKeys = [ForeignKey(
        entity = WeatherLocalResponse::class,
        parentColumns = ["id"],
        childColumns = ["weatherId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalLocation(
    val country: String = "",
    val name: String = "",
    val lon: Double = 0.0,
    val region: String = "",
    val lat: Double = 0.0,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var weatherId: Long = 0
)

@Entity(
    tableName = "forecast_weather",
    foreignKeys = [ForeignKey(
        entity = WeatherLocalResponse::class,
        parentColumns = ["id"],
        childColumns = ["weatherId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalForecastDayItem(
    var date: String = "",
    @Ignore val astro: LocalAstro = LocalAstro(),
    @Ignore val hour: List<LocalHourItem> = arrayListOf(),
    @Ignore val day: LocalDay = LocalDay(),
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var weatherId: Long = 0
)

@Entity(
    tableName = "astro",
    foreignKeys = [ForeignKey(
        entity = LocalForecastDayItem::class,
        parentColumns = ["id"],
        childColumns = ["forecastId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalAstro(
    val moonSet: String = "",
    val moonIllumination: String = "",
    val sunrise: String = "",
    val moonPhase: String = "",
    val sunset: String = "",
    val moonrise: String = "",
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var forecastId: Long = 0
)

@Entity(
    tableName = "day_weather",
    foreignKeys = [ForeignKey(
        entity = LocalForecastDayItem::class,
        parentColumns = ["id"],
        childColumns = ["forecastId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalDay(
    val uv: Int = 0,
    val avgTempC: Int = 0,
    val maxTempC: Int = 0,
    val minTempC: Int = 0,
    val avgHumidity: Int = 0,
    val conditionCode: Int = 0,
    val conditionIcon: String = "",
    val conditionText: String = "",
    val maxWindKph: Int = 0,
    val dailyChanceOfSnow: Int = 0,
    val dailyWillItSnow: Int = 0,
    val dailyChanceOfRain: Int = 0,
    val dailyWillItRain: Int = 0,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var forecastId: Long = 0
)

@Entity(
    tableName = "hour_weather",
    foreignKeys = [ForeignKey(
        entity = LocalForecastDayItem::class,
        parentColumns = ["id"],
        childColumns = ["forecastId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )]
)
data class LocalHourItem(
    val feelsLikeC: Int = 0,
    val windchillC: Int = 0,
    val tempC: Int = 0,
    val cloud: Int = 0,
    val windKph: Int = 0,
    val humidity: Int = 0,
    val willItRain: Int = 0,
    val uv: Int = 0,
    val heatIndexC: Int = 0,
    val chanceOfRain: Int = 0,
    val gustKph: Int = 0,
    val precipMm: Int = 0,
    val conditionCode: Int = 0,
    val conditionIcon: String = "",
    val conditionText: String = "",
    val willItSnow: Int = 0,
    val visKm: Int = 0,
    val timeEpoch: Int = 0,
    val time: String = "",
    val chanceOfSnow: Int = 0,
    val pressureMb: Int = 0,
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var forecastId: Long = 0
)