package com.example.weatherapplication.data.repo

import android.util.Log
import com.example.weatherapplication.core.pref.ApplicationSharedPrefManager
import com.example.weatherapplication.data.DataSourceResponseWrapper
import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToLocal
import com.example.weatherapplication.data.model.local.WeatherLocalResponse
import com.example.weatherapplication.data.source.SafeDataSourceCalls
import com.example.weatherapplication.data.source.api.WeatherApiDataSource
import com.example.weatherapplication.data.source.local.WeatherLocalDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Repository class to retrieve weather data whether from api or from local data sources.
 * @param safeDataSourceCalls class used to safely call data sources. It converts success or error responses to [DataSourceResponseWrapper] class.
 * @param weatherApiDataSource class which makes api calls to get weather forecast data from Weather Service.
 * @param weatherLocalDatasource class which makes local calls to cache or get weather forecast data from internal db.
 * @param applicationSharedPrefManager class to save the name of latest loaded location.
 */
class WeatherDataRepository @Inject constructor(
    private val safeDataSourceCalls: SafeDataSourceCalls,
    private val weatherApiDataSource: WeatherApiDataSource,
    private val weatherLocalDatasource: WeatherLocalDatasource,
    private val applicationSharedPrefManager: ApplicationSharedPrefManager
) {

    /**
     * Holds state of local data loading.
     * It is TRUE if the local data source is working and FALSE if the local data source finishes its job.
     */
    private var localDataLoading: Boolean = true

    /**
     * Holds state of api data loading.
     * It is TRUE if the api data source is working and FALSE if the api data source finishes its job.
     */
    private var apiDataLoading: Boolean = true

    /**
     * Brings data from local (if there are any cached data) and api (if network is accessible)
     * @param search search value of requested location (it can be city name or Lat,Lng combined)
     * @param days number of days which we want to forecast
     * @param aqi whether we want weather air quality in response
     * @param alerts weather we want weather alerts or not
     * @param callback lambda function to send loaded data to upper layers (use cases)
     * It takes [DataSourceResponseWrapper] as a response and boolean (called stillLoading) to tell if there are still data loading.
     */
    suspend fun loadWeatherData(
        search: String,
        days: Int,
        aqi: Boolean,
        alerts: Boolean,
        callback: (response: DataSourceResponseWrapper<WeatherLocalResponse>, stillLoading: Boolean) -> Unit
    ) =
        coroutineScope {
            // initially load data from local and invoke call back to update upper layers in a new coroutine
            launch(Dispatchers.IO) {
                // Saying that data is being loaded from local
                localDataLoading = true
                val localResponse = safeDataSourceCalls.run { weatherLocalDatasource.fetchData(search) }
                // Local loading operation is just finished
                localDataLoading = false
                Log.i(TAG, "localDataLoading is false ------ stillLoading = ${stillLoading()}")
                // We don't need to handle errors returned from local db.
                // We just need local data if there is a data already stored in db.
                if (localResponse is DataSourceResponseWrapper.Success) {
                    callback(DataSourceResponseWrapper.Success(localResponse.result), stillLoading())
                }
            }.join() // we wait for the local request to finish before making api request. Because it omits unwanted error when using offline.
            // then launch safeApiCall in order to load newer data from server
            launch(Dispatchers.IO) {
                apiDataLoading = true
                val response = safeDataSourceCalls.run {
                    // the api call which will be safely called
                    weatherApiDataSource.getWeatherInfo(search, days, if (aqi) "yes" else "no", if (alerts) "yes" else "no").convertToLocal()
                }// Api loading operation is just finished
                apiDataLoading = false
                Log.i(TAG, "apiDataLoading is false ------ stillLoading = ${stillLoading()}")
                // checking whether the server responded successfully or not
                callback.invoke(
                    when (response) {
                        is DataSourceResponseWrapper.Success<WeatherLocalResponse> -> {
                            applicationSharedPrefManager.lastLocationLoadedName = response.result.location.name
                            weatherLocalDatasource.cacheData(response.result)
                            response
                        }
                        is DataSourceResponseWrapper.Error -> response
                    }, stillLoading()
                )
            }
        }

    /**
     * Check whether data is loading from data sources or not
     */
    private fun stillLoading(): Boolean = localDataLoading || apiDataLoading

    companion object {
        const val TAG = "WeatherDataRepository"
    }
}