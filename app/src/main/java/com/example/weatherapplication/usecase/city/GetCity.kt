package com.example.weatherapplication.usecase.city

import com.example.weatherapplication.data.repo.CityDataRepository
import com.example.weatherapplication.usecase.UseCase
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetCity @Inject constructor(
    private val cityDataRepository: CityDataRepository
) : UseCase<GetCity.CityRequestValues, GetCity.CityResponseValue> {

    override suspend fun run(
        requestValues: CityRequestValues,
        callback: (response: UseCaseResponseWrapper<CityResponseValue>, stillLoading: Boolean) -> Unit
    ) {
        coroutineScope {
            launch(Dispatchers.IO) {
                callback.invoke(
                    UseCaseResponseWrapper.Success(CityResponseValue(cityDataRepository.getCityById(requestValues.id).name)), false
                )
            }
        }
    }

    data class CityRequestValues(val id: Long) : UseCase.RequestValues

    data class CityResponseValue(val cityName: String) : UseCase.ResponseValue
}