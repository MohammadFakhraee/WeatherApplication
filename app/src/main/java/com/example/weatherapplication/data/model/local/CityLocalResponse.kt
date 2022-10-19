package com.example.weatherapplication.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local response of searching cities
 */
@Entity(tableName = "city")
data class CityLocalResponse(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val serverId: Long = -1,
    val name: String = "",
    val region: String = "",
    val country: String = "",
    val lat: Float = 0f,
    val lon: Float = 0f,
    val url: String = ""
)