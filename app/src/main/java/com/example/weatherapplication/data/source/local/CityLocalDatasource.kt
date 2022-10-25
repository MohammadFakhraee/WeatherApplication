package com.example.weatherapplication.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.model.local.CityLocalResponse
import com.example.weatherapplication.util.sqlContains
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CityLocalDatasource @Inject constructor(
    private val cityLocalDao: CityLocalDao
) {

    /**
     * Takes api result which is just converted to local response and saves each item in local db if city name was not saved before.
     * It only sets local city id if the city name was saved in db before.
     */
    suspend fun cacheData(cities: List<CityLocalResponse>) {
        coroutineScope {
            cities.forEach { cityItem ->
                launch(Dispatchers.IO) {
                    cityLocalDao.containsCity(cityItem.name)
                        ?.let { cityItem.id = it.id }
                        ?: cityLocalDao.saveCity(cityItem).also { id -> cityItem.id = id }
                }
            }
        }
    }

    suspend fun fetchData(search: String): List<CityLocalResponse> = cityLocalDao.searchCity(search.sqlContains())

    suspend fun fetchById(id: Long): CityLocalResponse = cityLocalDao.searchById(id)
}

@Dao
interface CityLocalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCity(cityLocalResponse: CityLocalResponse): Long

    @Query("SELECT * FROM city WHERE name LIKE :cityName")
    suspend fun containsCity(cityName: String): CityLocalResponse?

    @Query("SELECT * FROM city WHERE name LIKE :cityName")
    suspend fun searchCity(cityName: String): List<CityLocalResponse>

    @Query("SELECT * FROM city WHERE id LIKE :id")
    suspend fun searchById(id: Long): CityLocalResponse
}