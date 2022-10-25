package com.example.weatherapplication.usecase.weather

import com.example.weatherapplication.data.model.converter.WeatherConverter.convertToTodayWeatherUi
import com.example.weatherapplication.data.repo.WeatherDataRepository
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.usecase.UseCaseFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetForecastByGeo @Inject constructor(private val weatherDataRepository: WeatherDataRepository) :
    UseCaseFlow<GetForecastByGeo.ByGeoReqVal, GetForecastByGeo.ByGeoResponseVal> {

    override fun run(requestValue: ByGeoReqVal): Flow<ByGeoResponseVal> {
        val search = "${requestValue.lat},${requestValue.lon}"
        return weatherDataRepository
            .loadWeatherDataFlow(search, requestValue.days, requestValue.aqi, requestValue.alerts)
            .map { weatherDataRepository -> ByGeoResponseVal(weatherDataRepository.convertToTodayWeatherUi()) }
    }

    data class ByGeoReqVal(val lat: Double, val lon: Double, val days: Int, val aqi: Boolean, val alerts: Boolean) : UseCaseFlow.RequestValues
    data class ByGeoResponseVal(val weatherTodayUi: WeatherTodayUi) : UseCaseFlow.ResponseValue
}