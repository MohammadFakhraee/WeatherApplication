package com.example.weatherapplication.usecase.weather

import com.example.weatherapplication.data.DataSourceResponseWrapper
import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToCurrentWeatherUi
import com.example.weatherapplication.data.repo.WeatherDataRepository
import com.example.weatherapplication.feature.widget.uistate.WeatherCurrentUi
import com.example.weatherapplication.usecase.UseCase
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import javax.inject.Inject

class GetCurrentByCity @Inject constructor(private val weatherDataRepository: WeatherDataRepository) :
    UseCase<GetCurrentByCity.ByCityReqVal, GetCurrentByCity.ByCityResVal> {

    override suspend fun run(requestValues: ByCityReqVal, callback: (response: UseCaseResponseWrapper<ByCityResVal>, stillLoading: Boolean) -> Unit) {
        weatherDataRepository.loadWeatherData(requestValues.cityName, days = 1, aqi = false, alerts = false) { response, stillLoading ->
            when (response) {
                is DataSourceResponseWrapper.Success ->
                    callback.invoke(UseCaseResponseWrapper.Success(ByCityResVal(response.result.convertToCurrentWeatherUi())), stillLoading)
                is DataSourceResponseWrapper.Error ->
                    callback.invoke(UseCaseResponseWrapper.Error(response.throwable), stillLoading)
            }
        }
    }

    data class ByCityReqVal(val cityName: String) : UseCase.RequestValues

    data class ByCityResVal(val weatherCurrentUi: WeatherCurrentUi) : UseCase.ResponseValue
}