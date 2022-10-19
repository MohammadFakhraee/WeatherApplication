package com.example.weatherapplication.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.model.local.CityLocalResponse

@Dao
interface CityLocalDatasource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCity(cityLocalResponse: CityLocalResponse): Long

    @Query("SELECT * FROM city WHERE name LIKE :cityName")
    suspend fun containsCity(cityName: String): CityLocalResponse?

    @Query("SELECT * FROM city WHERE name LIKE :cityName")
    suspend fun searchCity(cityName: String): List<CityLocalResponse>

    @Query("SELECT * FROM city WHERE id LIKE :id")
    suspend fun searchById(id: Long): CityLocalResponse

    @Query("SELECT * FROM city")
    suspend fun getAll(): List<CityLocalResponse>
}