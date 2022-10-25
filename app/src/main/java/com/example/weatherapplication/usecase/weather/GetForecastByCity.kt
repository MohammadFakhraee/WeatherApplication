package com.example.weatherapplication.usecase.weather

import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToTodayWeatherUi
import com.example.weatherapplication.data.repo.WeatherDataRepository
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.usecase.UseCaseFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetForecastByCity @Inject constructor(private val weatherDataRepository: WeatherDataRepository) :
    UseCaseFlow<GetForecastByCity.ByCityReqVal, GetForecastByCity.ByCityResponseVal> {

    override fun run(requestValue: ByCityReqVal): Flow<ByCityResponseVal> =
        weatherDataRepository
            .loadWeatherDataFlow(requestValue.cityName, requestValue.days, requestValue.aqr, requestValue.alerts)
            .map { weatherLocalResponse -> ByCityResponseVal(weatherLocalResponse.convertToTodayWeatherUi()) }

    data class ByCityReqVal(val cityName: String, val days: Int, val aqr: Boolean, val alerts: Boolean) : UseCaseFlow.RequestValues
    data class ByCityResponseVal(val weatherTodayUi: WeatherTodayUi) : UseCaseFlow.ResponseValue
}