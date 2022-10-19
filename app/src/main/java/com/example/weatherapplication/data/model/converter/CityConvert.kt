package com.example.weatherapplication.data.model.converter

import com.example.weatherapplication.data.model.api.CityApiResponse
import com.example.weatherapplication.data.model.local.CityLocalResponse
import com.example.weatherapplication.feature.search.uimodel.CitySearchUi

/**
 * Converter class for City objects.
 */
object CityConvert {

    /**
     * Converts api response of city object to local response which will be stored in local db
     */
    fun CityApiResponse.convertToLocalCityResponse(): CityLocalResponse =
        CityLocalResponse(
            0, id?.toLong() ?: 0, name ?: "", region ?: "", country ?: "",
            lat ?: 0f, lon ?: 0f, url ?: ""
        )

    /**
     * Converts local response of city object to UI mode which will be used in VIEW layer.
     */
    fun CityLocalResponse.convertToCitySearchUi(search: String): CitySearchUi =
        CitySearchUi(id, search, name.replace(search, "", true))
}