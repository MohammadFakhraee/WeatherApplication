package com.example.weatherapplication.data.model.converter

import android.icu.util.Calendar
import com.example.weatherapplication.data.model.api.ApiWeatherResponse
import com.example.weatherapplication.data.model.local.*
import com.example.weatherapplication.feature.today.uimodel.CurrentWeatherUi
import com.example.weatherapplication.feature.today.uimodel.ForecastDayUi
import com.example.weatherapplication.feature.today.uimodel.HourUi
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.feature.widget.uistate.WidgetWeatherCurrentUi
import com.example.weatherapplication.util.toCalendar

/**
 * Converter class for weather response
 */
object WeatherConverter {

    // Converts api response of weather forecasting to local weather object which will be stored in local db
    fun ApiWeatherResponse.convertToLocal(): WeatherLocalResponse {
        val localCurrent: LocalCurrent = current?.let {
            LocalCurrent(
                it.feelsLikeC?.toInt() ?: 0, it.uv ?: 0, it.tempC?.toInt() ?: 0, it.pressureMb?.toInt() ?: 0,
                it.gustKph?.toInt() ?: 0, it.cloud ?: 0, it.windKph?.toInt() ?: 0, it.condition?.code ?: 0,
                if (it.condition?.icon != null) "https:${it.condition.icon}" else "", it.condition?.text ?: "",
                it.visKm?.toInt() ?: 0, it.humidity ?: 0
            )
        } ?: LocalCurrent()

        val localLocation: LocalLocation = location?.let {
            LocalLocation(it.country ?: "", it.name ?: "", it.lon ?: 0.0, it.region ?: "", it.lat ?: 0.0)
        } ?: LocalLocation()

        val localForeCastDayItems: ArrayList<LocalForecastDayItem> = arrayListOf()

        forecast?.forecastDay?.let { apiForecastDayItems ->
            for (apiForeDay in apiForecastDayItems) {
                apiForeDay?.let {
                    val localAstro = LocalAstro(
                        it.astro?.moonSet ?: "", it.astro?.moonIllumination ?: "",
                        it.astro?.sunrise ?: "", it.astro?.moonPhase ?: "",
                        it.astro?.sunset ?: "", it.astro?.moonrise ?: ""
                    )

                    val hours: ArrayList<LocalHourItem> = arrayListOf()
                    it.hour?.let { hoursItem ->
                        for (item in hoursItem) {
                            item?.let { hourItem ->

                                val localHourItem = LocalHourItem(
                                    hourItem.feelsLikeC?.toInt() ?: 0, hourItem.windchillC?.toInt() ?: 0,
                                    hourItem.tempC?.toInt() ?: 0, hourItem.cloud ?: 0, hourItem.windKph?.toInt() ?: 0,
                                    hourItem.humidity ?: 0, hourItem.willItRain ?: 0, hourItem.uv?.toFloat()?.toInt() ?: 0,
                                    hourItem.heatIndexC?.toInt() ?: 0, hourItem.chanceOfRain ?: 0,
                                    hourItem.gustKph?.toInt() ?: 0, hourItem.precipMm?.toInt() ?: 0,
                                    hourItem.condition?.code ?: 0,
                                    if (hourItem.condition?.icon != null) "https:${hourItem.condition.icon}" else "",
                                    hourItem.condition?.text ?: "",
                                    hourItem.willItSnow ?: 0, hourItem.visKm?.toInt() ?: 0, hourItem.timeEpoch ?: 0,
                                    hourItem.time ?: "", hourItem.chanceOfSnow ?: 0,
                                    hourItem.pressureMb?.toFloat()?.toInt() ?: 0
                                )
                                hours.add(localHourItem)
                            }
                        }
                    }

                    val localDay = it.day?.let { day ->
                        LocalDay(
                            day.uv ?: 0, day.avgTempC?.toInt() ?: 0, day.maxTempC?.toInt() ?: 0,
                            day.minTempC?.toInt() ?: 0, day.avgHumidity ?: 0, day.condition?.code ?: 0,
                            if (day.condition?.icon != null) "https:${day.condition.icon}" else "", day.condition?.text ?: "",
                            day.maxWindKph?.toInt() ?: 0, day.dailyChanceOfSnow ?: 0,
                            day.dailyWillItSnow ?: 0, day.dailyChanceOfRain ?: 0, day.dailyChanceOfRain ?: 0
                        )
                    } ?: LocalDay()

                    localForeCastDayItems.add(LocalForecastDayItem(it.date ?: "", localAstro, hours, localDay))
                }
            }
        }

        return WeatherLocalResponse(localCurrent, localLocation, localForeCastDayItems)
    }

    /**
     * Converts local response of weather forecasting to [WeatherTodayUi] model which will be used in the main app page.
     */
    fun WeatherLocalResponse.convertToTodayWeatherUi(): WeatherTodayUi {
        val currentWeatherUi = CurrentWeatherUi(
            location.name,
            forecastDay[0].date.toCalendar("yyyy-MM-dd"),
            current.conditionIcon,
            current.conditionText,
            current.tempC,
            forecastDay[0].day.minTempC,
            forecastDay[0].day.maxTempC,
            current.feelsLikeC,
            current.windKph,
            current.humidity,
            current.pressureMb
        )

        val hourUiList = arrayListOf<HourUi>()
        forecastDay[0].hour.forEachIndexed { index, localHour ->
            val hourUi = HourUi(
                localHour.conditionIcon,
                localHour.time.toCalendar("yyyy-MM-dd HH:mm").get(Calendar.HOUR),
                index < 12,
                localHour.tempC
            )
            hourUiList.add(hourUi)
        }

        val forecastDayUiList = arrayListOf<ForecastDayUi>()

        forecastDay.forEachIndexed { index, localForecastDay ->
            localForecastDay.day
                .takeIf { index != 0 }
                ?.let {
                    forecastDayUiList.add(
                        ForecastDayUi(localForecastDay.date.toCalendar("yyyy-MM-dd"), it.conditionIcon, it.maxTempC, it.minTempC)
                    )
                }
        }

        return WeatherTodayUi(currentWeatherUi, hourUiList, forecastDayUiList)
    }

    /**
     * Converts local response of weather forecasting to [WidgetWeatherCurrentUi] model which will be used in places
     * that does not need full information.
     */
    fun WeatherLocalResponse.convertToCurrentWeatherUi(): WidgetWeatherCurrentUi {
        return WidgetWeatherCurrentUi(
            id = id,
            cityName = location.name,
            currentTempC = current.tempC,
            iconUrl = current.conditionIcon
        )
    }
}