package com.example.weatherapplication.data.source.local

import android.content.res.Resources
import android.util.Log
import androidx.room.*
import com.example.weatherapplication.data.model.local.*
import com.example.weatherapplication.data.source.local.relation.LocalWeatherRelationDetails
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Local weather data source used for caching/fetching data to/from local db.
 */
class WeatherLocalDatasource @Inject constructor(
    private val weatherResponseLocalDatasource: WeatherResponseLocalDatasource,
    private val locationLocalDatasource: LocationLocalDatasource,
    private val currentLocalDatasource: CurrentLocalDatasource,
    private val forecastLocalDatasource: ForecastLocalDatasource,
    private val astroLocalDatasource: AstroLocalDatasource,
    private val dayLocalDatasource: DayLocalDatasource,
    private val hourLocalDatasource: HourLocalDatasource
) {
    companion object {
        const val TAG = "WeatherLocalDatasource"
    }

    /**
     * Takes converted weather response and saves its data to db. At first stores [WeatherLocalResponse] and takes the returned id,
     * puts the id to internal properties and saves them too.
     */
    suspend fun cacheData(weatherLocalResponse: WeatherLocalResponse) =
        coroutineScope {
            launch {
                Log.i(TAG, "cacheData: starting cache process....")
                // Checks if the city's weather data has been saved already; if it was, removes the cached data.
                // It will also delete its properties saved in db, because we've used foreign key to create their relations.
                locationLocalDatasource.findLocation(weatherLocalResponse.location.name)?.let {
                    Log.i(TAG, "cacheData: removing previous cached data with weatherId: ${it.weatherId}")
                    weatherResponseLocalDatasource.delete(weatherLocalResponse.apply { id = it.weatherId })
                }
                // Saves new weather response inside db and puts the return value in weatherId
                val weatherId = weatherResponseLocalDatasource.save(weatherLocalResponse)
                Log.i(TAG, "cacheData: LocalWeatherResponse saved in db successfully with id: $weatherId")
                launch {
                    // Saves LocationLocal response in db with corresponding weatherId
                    val locationId = locationLocalDatasource.save(weatherLocalResponse.location.also { it.weatherId = weatherId })
                    Log.i(TAG, "cacheData: LocalLocation saved in db successfully with id: $locationId")
                }
                launch {
                    // saves CurrentLocal response with corresponding weatherId
                    val currentId = currentLocalDatasource.save(weatherLocalResponse.current.also { it.weatherId = weatherId })
                    Log.i(TAG, "cacheData: LocalCurrent saved in db successfully with id: $currentId")
                }
                weatherLocalResponse.forecastDay.forEach { forecastDay ->
                    // Saves ForecastLocal item with corresponding weatherId and puts return value in forecastId
                    val forecastId = forecastLocalDatasource.save(forecastDay.also { it.weatherId = weatherId })
                    Log.i(TAG, "cacheData: LocalForecastDayItem saved in db successfully with id: $forecastId")
                    // Saves AstroLocal item with corresponding forecastId
                    launch {
                        val astroId = astroLocalDatasource.save(forecastDay.astro.also { it.forecastId = forecastId })
                        Log.i(TAG, "cacheData: LocalAstro saved in db successfully with id: $astroId")
                    }
                    // Saves DayLocal item with corresponding forecastId
                    launch {
                        val dayId = dayLocalDatasource.save(forecastDay.day.also { it.forecastId = forecastId })
                        Log.i(TAG, "cacheData: LocalDay saved in db successfully with id: $dayId")
                    }
                    forecastDay.hour.forEach { hourItem ->
                        // Saves HourLocal item of each day with corresponding forecastId
                        val hourId = launch { hourLocalDatasource.save(hourItem.also { hourItem.forecastId = forecastId }) }
                        Log.i(TAG, "cacheData: LocalHourItem saved in db successfully with id: $hourId")
                    }
                }
            }
        }

    /**
     * Fetches stored data corresponding to search parameter. It uses relation to query for all data required.
     */
    suspend fun fetchData(search: String): WeatherLocalResponse {
        locationLocalDatasource.findLocation(search)?.let {
            val localWeatherRelationDetails = weatherResponseLocalDatasource.findWeather(it.weatherId)
            return localWeatherRelationDetails.weatherLocalResponse.copy(
                current = localWeatherRelationDetails.localCurrent,
                location = localWeatherRelationDetails.localLocation,
                forecastDay = localWeatherRelationDetails.forecastRelationDetails.map { forecastRelationDetails ->
                    forecastRelationDetails.localForecastDayItem.copy(
                        astro = forecastRelationDetails.localAstro,
                        day = forecastRelationDetails.localDay,
                        hour = forecastRelationDetails.localHourItemList
                    )
                }
            )
        } ?: throw Resources.NotFoundException("Couldn't find any data related to parameter search: $search")
    }
}

@Dao
interface WeatherResponseLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(weatherLocalResponse: WeatherLocalResponse): Long

    @Transaction
    @Query("SELECT * FROM weather WHERE id = :weatherId")
    suspend fun findWeather(weatherId: Long): LocalWeatherRelationDetails

    @Delete
    suspend fun delete(weatherLocalResponse: WeatherLocalResponse)
}

@Dao
interface LocationLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localLocation: LocalLocation): Long

    @Transaction
    @Query("SELECT * FROM current_location WHERE name LIKE :locationName")
    suspend fun findLocation(locationName: String): LocalLocation?
}

@Dao
interface CurrentLocalDatasource {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localCurrent: LocalCurrent): Long
}

@Dao
interface ForecastLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localForecastDayItem: LocalForecastDayItem): Long
}

@Dao
interface AstroLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localAstro: LocalAstro): Long
}

@Dao
interface DayLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localDay: LocalDay): Long
}

@Dao
interface HourLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localHourItem: LocalHourItem): Long
}