package com.example.weatherapplication.data.source.api

import com.example.weatherapplication.data.model.api.ApiWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiDataSource {
    @GET("forecast.json")
    suspend fun getWeatherInfo(
        @Query("q") search: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
//        @Query("key") key: String = "8e603f4c482c4e3cbc7121711222409"
    ): ApiWeatherResponse
}
