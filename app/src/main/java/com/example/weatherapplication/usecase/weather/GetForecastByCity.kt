package com.example.weatherapplication.usecase.weather

import com.example.weatherapplication.data.DataSourceResponseWrapper
import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToTodayWeatherUi
import com.example.weatherapplication.data.model.local.WeatherLocalResponse
import com.example.weatherapplication.data.repo.WeatherDataRepository
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.usecase.UseCase
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import javax.inject.Inject

class GetForecastByCity @Inject constructor(private val weatherDataRepository: WeatherDataRepository) :
    UseCase<GetForecastByCity.ByCityReqVal, GetForecastByCity.ByCityResponseVal> {

    override suspend fun run(
        requestValues: ByCityReqVal,
        callback: (response: UseCaseResponseWrapper<ByCityResponseVal>, stillLoading: Boolean) -> Unit
    ) {
        requestValues.let {
            weatherDataRepository.loadWeatherData(it.cityName, it.days, it.aqr, it.alerts) { response, stillLoading ->
                when (response) {
                    is DataSourceResponseWrapper.Success<WeatherLocalResponse> ->
                        callback.invoke(UseCaseResponseWrapper.Success(ByCityResponseVal(response.result.convertToTodayWeatherUi())), stillLoading)
                    is DataSourceResponseWrapper.Error -> callback.invoke(UseCaseResponseWrapper.Error(response.throwable), stillLoading)
                }
            }
        }
    }

    data class ByCityReqVal(val cityName: String, val days: Int, val aqr: Boolean, val alerts: Boolean) : UseCase.RequestValues
    data class ByCityResponseVal(val weatherTodayUi: WeatherTodayUi) : UseCase.ResponseValue
}