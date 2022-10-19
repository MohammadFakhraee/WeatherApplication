package com.example.weatherapplication.feature.today.uimodel

import android.icu.util.Calendar
import com.example.weatherapplication.feature.today.WeatherTodayFragment
import com.example.weatherapplication.feature.today.WeatherTodayViewModel
import com.example.weatherapplication.usecase.weather.GetForecastByCity
import com.example.weatherapplication.usecase.weather.GetForecastByGeo

/**
 * Data class used in view and presenter layers: [WeatherTodayFragment], [WeatherTodayViewModel], [GetForecastByCity] and [GetForecastByGeo]
 */
data class WeatherTodayUi(val currentWeatherUi: CurrentWeatherUi, val hourUiList: List<HourUi>, val forecastDayUiList: List<ForecastDayUi>)

/**
 * Data class used in [WeatherTodayUi]
 */
data class CurrentWeatherUi(
    val locationName: String,
    val date: Calendar,
    val icon: String,
    val conditionText: String,
    val tempC: Int,
    val minTempC: Int,
    val maxTempC: Int,
    val feelsLikeC: Int,
    val windSpeedKph: Int,
    val humidity: Int,
    val pressureMb: Int
)

/**
 * Data class used in [WeatherTodayUi]
 */
data class HourUi(val icon: String, val time: Int, val isBeforeNoon: Boolean, val tempC: Int)

/**
 * Data class used in [WeatherTodayUi]
 */
data class ForecastDayUi(val date: Calendar, val icon: String, val maxTempC: Int, val minTempC: Int)
