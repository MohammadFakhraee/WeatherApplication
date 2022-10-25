package com.example.weatherapplication.data.repo

import com.example.weatherapplication.data.model.converter.CityConvert.convertToLocalCityResponse
import com.example.weatherapplication.data.model.local.CityLocalResponse
import com.example.weatherapplication.data.source.api.CityApiDatasource
import com.example.weatherapplication.data.source.local.CityLocalDatasource
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Repository class to retrieve weather data whether from api or from local data sources.
 * @param cityApiDataSource class which makes api calls to search city from Weather Service.
 * @param cityLocalDatasource class which makes local calls to cache search city data to internal db.
 */
class CityDataRepository @Inject constructor(
    private val cityApiDataSource: CityApiDatasource,
    private val cityLocalDatasource: CityLocalDatasource
) {

    /**
     * Searches cities from api and caches result to db
     * @param searchField parameter to search for cities
     * @return kotlin flow of [CityLocalResponse] model
     */
    fun searchCitiesFlow(searchField: String): Flow<List<CityLocalResponse>> =
        cityApiDataSource
            .searchLocations(searchField)
            .map { cityApiResponses -> cityApiResponses.map { cityApiResponse -> cityApiResponse.convertToLocalCityResponse() } }
            .onEach { cityLocalResponses -> cityLocalDatasource.cacheData(cityLocalResponses) }
            .catch { emit(cityLocalDatasource.fetchData(searchField)) }

    /**
     * returns an specific city from local db with requested id.
     */
    fun getCityById(id: Long): Flow<CityLocalResponse> = flow {
        emit(cityLocalDatasource.fetchById(id))
    }
}