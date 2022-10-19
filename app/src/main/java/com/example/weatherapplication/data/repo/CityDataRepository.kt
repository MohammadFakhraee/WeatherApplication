package com.example.weatherapplication.data.repo

import com.example.weatherapplication.data.DataSourceResponseWrapper
import com.example.weatherapplication.data.model.converter.CityConvert.convertToLocalCityResponse
import com.example.weatherapplication.data.model.local.CityLocalResponse
import com.example.weatherapplication.data.source.SafeDataSourceCalls
import com.example.weatherapplication.data.source.api.CityApiDatasource
import com.example.weatherapplication.data.source.local.CityLocalDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Repository class to retrieve weather data whether from api or from local data sources.
 * @param safeDataSourceCalls class used to safely call data sources. It converts success or error responses to [DataSourceResponseWrapper] class.
 * @param cityApiDatasource class which makes api calls to search city from Weather Service.
 * @param cityLocalDatasource class which makes local calls to cache search city data to internal db.
 */
class CityDataRepository @Inject constructor(
    private val safeDataSourceCalls: SafeDataSourceCalls,
    private val cityApiDatasource: CityApiDatasource,
    private val cityLocalDatasource: CityLocalDatasource
) {

    /**
     * Searches cities from api and caches result to db
     * @param searchField parameter to search for cities
     * @param callback lambda function to send loaded data to upper layers (use cases)
     * It takes [DataSourceResponseWrapper] as response
     */
    suspend fun searchCities(searchField: String, callback: (DataSourceResponseWrapper<List<CityLocalResponse>>) -> Unit) =
        coroutineScope {
            //creating new coroutine and calling api for city search
            launch(Dispatchers.IO) {
                val response = safeDataSourceCalls.run {
                    cityApiDatasource.searchLocations(searchField).map { it.convertToLocalCityResponse() }
                }
                callback.invoke(
                    when (response) {
                        is DataSourceResponseWrapper.Success -> {
                            cacheCities(response.result)
                            response
                        }
                        is DataSourceResponseWrapper.Error -> response
                    }
                )
            }
        }

    /**
     * returns an specific city from local db with requested id.
     */
    suspend fun getCityById(id: Long): CityLocalResponse {
        return cityLocalDatasource.searchById(id)
    }

    /**
     * Takes api result and saves each item in local db if city name was not saved before.
     * It only sets local city id if the city name was saved in db before.
     */
    private suspend fun cacheCities(cities: List<CityLocalResponse>) =
        coroutineScope {
            cities.forEach { cityItem ->
                launch(Dispatchers.IO) {
                    cityLocalDatasource.containsCity(cityItem.name)
                        ?.let { cityItem.id = it.id }
                        ?: cityLocalDatasource.saveCity(cityItem).also { id -> cityItem.id = id }
                }
            }
        }

}
