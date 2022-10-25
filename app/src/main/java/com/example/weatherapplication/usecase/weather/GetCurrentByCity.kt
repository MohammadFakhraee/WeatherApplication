package com.example.weatherapplication.usecase.weather

import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToCurrentWeatherUi
import com.example.weatherapplication.data.repo.WeatherDataRepository
import com.example.weatherapplication.feature.widget.uistate.WeatherCurrentUi
import com.example.weatherapplication.usecase.UseCaseFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentByCity @Inject constructor(private val weatherDataRepository: WeatherDataRepository) :
    UseCaseFlow<GetCurrentByCity.ByCityReqVal, GetCurrentByCity.ByCityResVal> {

    override fun run(requestValue: ByCityReqVal): Flow<ByCityResVal> =
        weatherDataRepository
            .loadWeatherDataFlow(requestValue.cityName, days = 1, aqi = false, alerts = false)
            .map { weatherLocalResponse -> ByCityResVal(weatherLocalResponse.convertToCurrentWeatherUi()) }

    data class ByCityReqVal(val cityName: String) : UseCaseFlow.RequestValues

    data class ByCityResVal(val weatherCurrentUi: WeatherCurrentUi) : UseCaseFlow.ResponseValue
}