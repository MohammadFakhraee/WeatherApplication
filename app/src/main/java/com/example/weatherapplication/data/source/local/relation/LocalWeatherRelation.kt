package com.example.weatherapplication.data.source.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.weatherapplication.data.model.local.*

/**
 * A  class used by [Relation] to query a relation between [WeatherLocalResponse] and [LocalCurrent], [LocalLocation] & [LocalForecastDayItem]
 */
data class LocalWeatherRelationDetails(
    @Embedded var weatherLocalResponse: WeatherLocalResponse,
    @Relation(entity = LocalCurrent::class, parentColumn = "id", entityColumn = "weatherId") var localCurrent: LocalCurrent,
    @Relation(entity = LocalLocation::class, parentColumn = "id", entityColumn = "weatherId") var localLocation: LocalLocation,
    @Relation(
        entity = LocalForecastDayItem::class,
        parentColumn = "id",
        entityColumn = "weatherId"
    ) var forecastRelationDetails: List<ForecastRelationDetails>
)

/**
 * A class used by [Relation] to query a relation between [LocalForecastDayItem] and [LocalDay], [LocalHourItem] & [LocalAstro]
 */
data class ForecastRelationDetails(
    @Embedded var localForecastDayItem: LocalForecastDayItem,
    @Relation(entity = LocalDay::class, parentColumn = "id", entityColumn = "forecastId") var localDay: LocalDay,
    @Relation(entity = LocalAstro::class, parentColumn = "id", entityColumn = "forecastId") var localAstro: LocalAstro,
    @Relation(entity = LocalHourItem::class, parentColumn = "id", entityColumn = "forecastId") var localHourItemList: List<LocalHourItem>
)