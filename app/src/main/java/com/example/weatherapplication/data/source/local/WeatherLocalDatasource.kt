package com.example.weatherapplication.data.source.local

import android.content.res.Resources
import androidx.room.*
import com.example.weatherapplication.data.model.local.*
import com.example.weatherapplication.data.source.local.relation.LocalWeatherRelationDetails
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Local weather data source used for caching/fetching data to/from local db.
 */
@Suppress("OPT_IN_IS_NOT_ENABLED")
class WeatherLocalDatasource @Inject constructor(
    private val weatherResponseLocalDao: WeatherResponseLocalDao,
    private val locationLocalDao: LocationLocalDao,
    private val currentLocalDao: CurrentLocalDao,
    private val forecastLocalDao: ForecastLocalDao,
    private val astroLocalDao: AstroLocalDao,
    private val dayLocalDao: DayLocalDao,
    private val hourLocalDao: HourLocalDao
) {

    /**
     * Takes converted weather response and saves its data to db. At first stores [WeatherLocalResponse] and takes the returned id,
     * puts the id to internal properties and saves them too.
     */
    suspend fun cacheData(weatherLocalResponse: WeatherLocalResponse) =
        coroutineScope {
            launch {
                // Checks if the city's weather data has been saved already; if it was, removes the cached data.
                // It will also delete its properties saved in db, because we've used foreign key to create their relations.
//                locationLocalDao.findLocation(weatherLocalResponse.location.name)?.let {
//                    weatherResponseLocalDao.delete(weatherLocalResponse.apply { id = it.weatherId })
//                }
                // Saves new weather response inside db and puts the return value in weatherId
                val weatherId = weatherResponseLocalDao.save(weatherLocalResponse)
                launch {
                    // Saves LocationLocal response in db with corresponding weatherId
                    locationLocalDao.save(weatherLocalResponse.location.also { it.weatherId = weatherId })
                }
                launch {
                    // saves CurrentLocal response with corresponding weatherId
                    currentLocalDao.save(weatherLocalResponse.current.also { it.weatherId = weatherId })
                }
                weatherLocalResponse.forecastDay.forEach { forecastDay ->
                    // Saves ForecastLocal item with corresponding weatherId and puts return value in forecastId
                    val forecastId = forecastLocalDao.save(forecastDay.also { it.weatherId = weatherId })
                    // Saves AstroLocal item with corresponding forecastId
                    launch { astroLocalDao.save(forecastDay.astro.also { it.forecastId = forecastId }) }
                    // Saves DayLocal item with corresponding forecastId
                    launch { dayLocalDao.save(forecastDay.day.also { it.forecastId = forecastId }) }
                    forecastDay.hour.forEach { hourItem ->
                        // Saves HourLocal item of each day with corresponding forecastId
                        launch { hourLocalDao.save(hourItem.also { hourItem.forecastId = forecastId }) }
                    }
                }
            }
        }

    /**
     * Fetches stored data corresponding to search parameter. It uses relation to query for all data required.
     */
    suspend fun fetchData(search: String): WeatherLocalResponse {
        locationLocalDao.findLocation(search)?.let {
            val localWeatherRelationDetails = weatherResponseLocalDao.findWeather(it.weatherId)
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
interface WeatherResponseLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(weatherLocalResponse: WeatherLocalResponse): Long

    @Transaction
    @Query("SELECT * FROM weather WHERE id = :weatherId")
    suspend fun findWeather(weatherId: Long): LocalWeatherRelationDetails

    @Delete
    suspend fun delete(weatherLocalResponse: WeatherLocalResponse)
}

@Dao
interface LocationLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localLocation: LocalLocation): Long

    @Transaction
    @Query("SELECT * FROM current_location WHERE name LIKE :locationName")
    suspend fun findLocation(locationName: String): LocalLocation?
}

@Dao
interface CurrentLocalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localCurrent: LocalCurrent): Long
}

@Dao
interface ForecastLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localForecastDayItem: LocalForecastDayItem): Long
}

@Dao
interface AstroLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localAstro: LocalAstro): Long
}

@Dao
interface DayLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localDay: LocalDay): Long
}

@Dao
interface HourLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(localHourItem: LocalHourItem): Long
}