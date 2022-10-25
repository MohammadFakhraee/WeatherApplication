package com.example.weatherapplication.data.repo

import android.util.Log
import com.example.weatherapplication.core.pref.ApplicationSharedPrefManager
import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToLocal
import com.example.weatherapplication.data.model.local.WeatherLocalResponse
import com.example.weatherapplication.data.source.api.WeatherApiDatasource
import com.example.weatherapplication.data.source.local.WeatherLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Repository class to retrieve weather data whether from api or from local data sources.
 * @param weatherApiDatasource class which makes api calls to get weather forecast data from Weather Service.
 * @param weatherLocalDatasource class which makes local calls to cache or get weather forecast data from internal db.
 * @param applicationSharedPrefManager class to save the name of latest loaded location.
 */
class WeatherDataRepository @Inject constructor(
    private val weatherApiDatasource: WeatherApiDatasource,
    private val weatherLocalDatasource: WeatherLocalDatasource,
    private val applicationSharedPrefManager: ApplicationSharedPrefManager
) {

    /**
     * Brings data from local (if there are any cached data) and api (if network is accessible)
     * @param search search value of requested location (it can be city name or Lat,Lng combined)
     * @param days number of days which we want to forecast
     * @param aqi whether we want weather air quality in response
     * @param alerts weather we want weather alerts or not
     * @return kotlin flow of [WeatherLocalResponse] model
     */
    fun loadWeatherDataFlow(
        search: String,
        days: Int,
        aqi: Boolean,
        alerts: Boolean
    ): Flow<WeatherLocalResponse> {
        return weatherApiDatasource
            .getLatestWeatherApi(search, days, if (aqi) "yes" else "no", if (alerts) "yes" else "no")
            .map { weatherApiResponse -> weatherApiResponse.convertToLocal() }
            .onEach { weatherLocalResponse -> weatherLocalDatasource.cacheData(weatherLocalResponse) }
            .catch { emit(weatherLocalDatasource.fetchData(search)) }
            .onEach { Log.i("FlowBasedApp", "API - loadWeatherDataFlow: data has been cached in db") }
    }
}