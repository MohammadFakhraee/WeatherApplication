package com.example.weatherapplication.data.source.api

import com.example.weatherapplication.data.model.api.CityApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CityApiDatasource {
    @GET("search.json")
    suspend fun searchLocations(@Query("q") searchField: String): List<CityApiResponse>
}