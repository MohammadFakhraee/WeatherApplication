package com.example.weatherapplication.data.source.api

import com.example.weatherapplication.data.model.api.CityApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class CityApiDatasource @Inject constructor(private val cityApi: CityApi) {

    fun searchLocations(searchField: String): Flow<List<CityApiResponse>> = flow {
        val result = cityApi.searchLocations(searchField)
        emit(result)
    }
}

interface CityApi {
    @GET("search.json")
    suspend fun searchLocations(@Query("q") searchField: String): List<CityApiResponse>
}