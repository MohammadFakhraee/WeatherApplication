package com.example.weatherapplication.usecase.city

import com.example.weatherapplication.data.repo.CityDataRepository
import com.example.weatherapplication.usecase.UseCaseFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCity @Inject constructor(
    private val cityDataRepository: CityDataRepository
) : UseCaseFlow<GetCity.CityRequestValues, GetCity.CityResponseValue> {

    override fun run(requestValue: CityRequestValues): Flow<CityResponseValue> =
        cityDataRepository.getCityById(requestValue.id).map { CityResponseValue(it.name) }

    data class CityRequestValues(val id: Long) : UseCaseFlow.RequestValues

    data class CityResponseValue(val cityName: String) : UseCaseFlow.ResponseValue
}