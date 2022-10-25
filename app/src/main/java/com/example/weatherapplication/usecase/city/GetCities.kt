package com.example.weatherapplication.usecase.city

import com.example.weatherapplication.data.model.converter.CityConvert.convertToCitySearchUi
import com.example.weatherapplication.data.repo.CityDataRepository
import com.example.weatherapplication.feature.search.uimodel.CitySearchUi
import com.example.weatherapplication.usecase.UseCaseFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCities @Inject constructor(private val cityDataRepository: CityDataRepository) :
    UseCaseFlow<GetCities.CitySearchRequestValue, GetCities.CitySearchResponseValue> {

    override fun run(requestValue: CitySearchRequestValue): Flow<CitySearchResponseValue> =
        cityDataRepository
            .searchCitiesFlow(requestValue.searchField)
            .map { cityLocalResponses -> cityLocalResponses.map { it.convertToCitySearchUi(requestValue.searchField) } }
            .map { CitySearchResponseValue(it) }

    data class CitySearchRequestValue(val searchField: String) : UseCaseFlow.RequestValues

    data class CitySearchResponseValue(val citiesSearchUi: List<CitySearchUi>) : UseCaseFlow.ResponseValue
}
