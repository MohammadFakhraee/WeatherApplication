package com.example.weatherapplication.data.model.api

import com.google.gson.annotations.SerializedName

/**
 * Api response when requesting for weather of city or specific location
 */
data class ApiWeatherResponse(

    @field:SerializedName("current")
    val current: ApiCurrent? = null,

    @field:SerializedName("location")
    val location: ApiLocation? = null,

    @field:SerializedName("forecast")
    val forecast: ApiForecast? = null
)

data class ApiCurrent(

    @field:SerializedName("feelslike_c")
    val feelsLikeC: Float? = null,

    @field:SerializedName("uv")
    val uv: Int? = null,

    @field:SerializedName("last_updated")
    val lastUpdated: String? = null,

    @field:SerializedName("feelslike_f")
    val feelsLikeF: Float? = null,

    @field:SerializedName("wind_degree")
    val windDegree: Float? = null,

    @field:SerializedName("last_updated_epoch")
    val lastUpdatedEpoch: Float? = null,

    @field:SerializedName("is_day")
    val isDay: Float? = null,

    @field:SerializedName("precip_in")
    val precipIn: Float? = null,

    @field:SerializedName("air_quality")
    val airQuality: ApiAirQuality? = null,

    @field:SerializedName("wind_dir")
    val windDir: String? = null,

    @field:SerializedName("gust_mph")
    val gustMph: Float? = null,

    @field:SerializedName("temp_c")
    val tempC: Float? = null,

    @field:SerializedName("pressure_in")
    val pressureIn: Float? = null,

    @field:SerializedName("gust_kph")
    val gustKph: Float? = null,

    @field:SerializedName("temp_f")
    val tempF: Float? = null,

    @field:SerializedName("precip_mm")
    val precipMm: Float? = null,

    @field:SerializedName("cloud")
    val cloud: Int? = null,

    @field:SerializedName("wind_kph")
    val windKph: Float? = null,

    @field:SerializedName("condition")
    val condition: ApiCondition? = null,

    @field:SerializedName("wind_mph")
    val windMph: Float? = null,

    @field:SerializedName("vis_km")
    val visKm: Float? = null,

    @field:SerializedName("humidity")
    val humidity: Int? = null,

    @field:SerializedName("pressure_mb")
    val pressureMb: Float? = null,

    @field:SerializedName("vis_miles")
    val visMiles: Float? = null
)

data class ApiAirQuality(

    @field:SerializedName("no2")
    val no2: Float? = null,

    @field:SerializedName("o3")
    val o3: Float? = null,

    @field:SerializedName("us-epa-index")
    val usEpaIndex: Int? = null,

    @field:SerializedName("so2")
    val so2: Float? = null,

    @field:SerializedName("pm2_5")
    val pm25: Float? = null,

    @field:SerializedName("pm10")
    val pm10: Float? = null,

    @field:SerializedName("co")
    val co: Float? = null,

    @field:SerializedName("gb-defra-index")
    val gbDefraIndex: Int? = null
)

data class ApiCondition(

    @field:SerializedName("code")
    val code: Int? = null,

    @field:SerializedName("icon")
    val icon: String? = null,

    @field:SerializedName("text")
    val text: String? = null
)

data class ApiForecastDayItem(

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("astro")
    val astro: ApiAstro? = null,

    @field:SerializedName("date_epoch")
    val dateEpoch: Int? = null,

    @field:SerializedName("hour")
    val hour: List<ApiHourItem?>? = null,

    @field:SerializedName("day")
    val day: ApiDay? = null
)

data class ApiLocation(

    @field:SerializedName("localtime")
    val localtime: String? = null,

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("localtime_epoch")
    val localtimeEpoch: Int? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("region")
    val region: String? = null,

    @field:SerializedName("lat")
    val lat: Double? = null,

    @field:SerializedName("tz_id")
    val tzId: String? = null
)

data class ApiHourItem(

    @field:SerializedName("feelslike_c")
    val feelsLikeC: Float? = null,

    @field:SerializedName("feelslike_f")
    val feelsLikeF: Float? = null,

    @field:SerializedName("wind_degree")
    val windDegree: Float? = null,

    @field:SerializedName("windchill_f")
    val windchillF: Float? = null,

    @field:SerializedName("windchill_c")
    val windchillC: Float? = null,

    @field:SerializedName("temp_c")
    val tempC: Float? = null,

    @field:SerializedName("temp_f")
    val tempF: Float? = null,

    @field:SerializedName("cloud")
    val cloud: Int? = null,

    @field:SerializedName("wind_kph")
    val windKph: Float? = null,

    @field:SerializedName("wind_mph")
    val windMph: Float? = null,

    @field:SerializedName("humidity")
    val humidity: Int? = null,

    @field:SerializedName("dewpoint_f")
    val dewPointF: Float? = null,

    @field:SerializedName("will_it_rain")
    val willItRain: Int? = null,

    @field:SerializedName("uv")
    val uv: Int? = null,

    @field:SerializedName("heatindex_f")
    val heatIndexF: Float? = null,

    @field:SerializedName("dewpoint_c")
    val dewPointC: Float? = null,

    @field:SerializedName("is_day")
    val isDay: Int? = null,

    @field:SerializedName("precip_in")
    val precipIn: Float? = null,

    @field:SerializedName("heatindex_c")
    val heatIndexC: Float? = null,

    @field:SerializedName("air_quality")
    val airQuality: ApiAirQuality? = null,

    @field:SerializedName("wind_dir")
    val windDir: String? = null,

    @field:SerializedName("gust_mph")
    val gustMph: Float? = null,

    @field:SerializedName("pressure_in")
    val pressureIn: Float? = null,

    @field:SerializedName("chance_of_rain")
    val chanceOfRain: Int? = null,

    @field:SerializedName("gust_kph")
    val gustKph: Float? = null,

    @field:SerializedName("precip_mm")
    val precipMm: Float? = null,

    @field:SerializedName("condition")
    val condition: ApiCondition? = null,

    @field:SerializedName("will_it_snow")
    val willItSnow: Int? = null,

    @field:SerializedName("vis_km")
    val visKm: Float? = null,

    @field:SerializedName("time_epoch")
    val timeEpoch: Int? = null,

    @field:SerializedName("time")
    val time: String? = null,

    @field:SerializedName("chance_of_snow")
    val chanceOfSnow: Int? = null,

    @field:SerializedName("pressure_mb")
    val pressureMb: Int? = null,

    @field:SerializedName("vis_miles")
    val visMiles: Float? = null
)

data class ApiAstro(

    @field:SerializedName("moonset")
    val moonSet: String? = null,

    @field:SerializedName("moon_illumination")
    val moonIllumination: String? = null,

    @field:SerializedName("sunrise")
    val sunrise: String? = null,

    @field:SerializedName("moon_phase")
    val moonPhase: String? = null,

    @field:SerializedName("sunset")
    val sunset: String? = null,

    @field:SerializedName("moonrise")
    val moonrise: String? = null
)

data class ApiDay(

    @field:SerializedName("avgvis_km")
    val avgVisKm: Float? = null,

    @field:SerializedName("uv")
    val uv: Int? = null,

    @field:SerializedName("avgtemp_f")
    val avgTempF: Float? = null,

    @field:SerializedName("avgtemp_c")
    val avgTempC: Float? = null,

    @field:SerializedName("daily_chance_of_snow")
    val dailyChanceOfSnow: Int? = null,

    @field:SerializedName("maxtemp_c")
    val maxTempC: Float? = null,

    @field:SerializedName("maxtemp_f")
    val maxTempF: Float? = null,

    @field:SerializedName("mintemp_c")
    val minTempC: Float? = null,

    @field:SerializedName("avgvis_miles")
    val avgVisMiles: Int? = null,

    @field:SerializedName("air_quality")
    val airQuality: ApiAirQuality? = null,

    @field:SerializedName("daily_will_it_rain")
    val dailyWillItRain: Int? = null,

    @field:SerializedName("mintemp_f")
    val minTempF: Float? = null,

    @field:SerializedName("totalprecip_in")
    val totalPrecipIn: Float? = null,

    @field:SerializedName("avghumidity")
    val avgHumidity: Int? = null,

    @field:SerializedName("condition")
    val condition: ApiCondition? = null,

    @field:SerializedName("maxwind_kph")
    val maxWindKph: Float? = null,

    @field:SerializedName("maxwind_mph")
    val maxWindMph: Float? = null,

    @field:SerializedName("daily_chance_of_rain")
    val dailyChanceOfRain: Int? = null,

    @field:SerializedName("totalprecip_mm")
    val totalPrecipMm: Float? = null,

    @field:SerializedName("daily_will_it_snow")
    val dailyWillItSnow: Int? = null
)

data class ApiForecast(

    @field:SerializedName("forecastday")
    val forecastDay: List<ApiForecastDayItem?>? = null
)
