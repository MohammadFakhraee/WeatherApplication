package com.example.weatherapplication.usecase.weather

import com.example.weatherapplication.data.DataSourceResponseWrapper
import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToTodayWeatherUi
import com.example.weatherapplication.data.model.local.WeatherLocalResponse
import com.example.weatherapplication.data.repo.WeatherDataRepository
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.usecase.UseCase
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import javax.inject.Inject

class GetForecastByGeo @Inject constructor(private val weatherDataRepository: WeatherDataRepository) :
    UseCase<GetForecastByGeo.ByGeoReqVal, GetForecastByGeo.ByGeoResponseVal> {

    override suspend fun run(
        requestValues: ByGeoReqVal,
        callback: (response: UseCaseResponseWrapper<ByGeoResponseVal>, stillLoading: Boolean) -> Unit
    ) {
        requestValues.let {
            val search = "${it.lat},${it.lon}"
            weatherDataRepository.loadWeatherData(search, it.days, it.aqi, it.alerts) { response, stillLoading ->
                when (response) {
                    is DataSourceResponseWrapper.Success<WeatherLocalResponse> ->
                        callback.invoke(UseCaseResponseWrapper.Success(ByGeoResponseVal(response.result.convertToTodayWeatherUi())), stillLoading)
                    is DataSourceResponseWrapper.Error -> callback.invoke(UseCaseResponseWrapper.Error(response.throwable), stillLoading)
                }
            }
        }
    }

    data class ByGeoReqVal(val lat: Double, val lon: Double, val days: Int, val aqi: Boolean, val alerts: Boolean) : UseCase.RequestValues

    data class ByGeoResponseVal(val weatherTodayUi: WeatherTodayUi) : UseCase.ResponseValue
}