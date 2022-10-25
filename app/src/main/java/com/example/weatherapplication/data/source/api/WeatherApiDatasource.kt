package com.example.weatherapplication.data.source.api

import android.util.Log
import com.example.weatherapplication.data.model.api.ApiWeatherResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class WeatherApiDatasource @Inject constructor(
    private val weatherApi: WeatherApi
) {

    fun getLatestWeatherApi(
        search: String,
        days: Int = 1,
        aqi: String = "no",
        alerts: String = "no",
        refreshInterval: Long = 5000
    ): Flow<ApiWeatherResponse> = flow {
        while (true) {
            Log.i("FlowBasedApp", "API - getLatestWeatherApi: starting to get updated weather info")
            val latestWeather = weatherApi.getWeatherInfo(search, days, aqi, alerts)
            Log.i("FlowBasedApp", "API - getLatestWeatherApi: result $latestWeather")
            emit(latestWeather)
            delay(refreshInterval)
        }
    }.onEach { Log.i("FlowBasedApp", "API - getLatestWeatherApi: $it") }
}

interface WeatherApi {
    @GET("forecast.json")
    suspend fun getWeatherInfo(
        @Query("q") search: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
//        @Query("key") key: String = "8e603f4c482c4e3cbc7121711222409"
    ): ApiWeatherResponse
}
