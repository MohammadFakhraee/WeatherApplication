package com.example.weatherapplication.usecase.city

import com.example.weatherapplication.data.DataSourceResponseWrapper
import com.example.weatherapplication.data.model.converter.CityConvert.convertToCitySearchUi
import com.example.weatherapplication.data.repo.CityDataRepository
import com.example.weatherapplication.feature.search.uimodel.CitySearchUi
import com.example.weatherapplication.usecase.UseCase
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import javax.inject.Inject

class GetCities @Inject constructor(private val cityDataRepository: CityDataRepository) :
    UseCase<GetCities.CitySearchRequestValue, GetCities.CitySearchResponseValue> {

    override suspend fun run(
        requestValues: CitySearchRequestValue,
        callback: (response: UseCaseResponseWrapper<CitySearchResponseValue>, stillLoading: Boolean) -> Unit
    ) {
        cityDataRepository.searchCities(requestValues.searchField) { response ->
            when (response) {
                is DataSourceResponseWrapper.Success -> callback(
                    UseCaseResponseWrapper.Success(
                        CitySearchResponseValue(response.result.map { it.convertToCitySearchUi(requestValues.searchField) })
                    ), false
                )
                is DataSourceResponseWrapper.Error -> UseCaseResponseWrapper.Error(response.throwable)
            }
        }
    }

    data class CitySearchRequestValue(val searchField: String) : UseCase.RequestValues

    data class CitySearchResponseValue(val citiesSearchUi: List<CitySearchUi>) : UseCase.ResponseValue
}
