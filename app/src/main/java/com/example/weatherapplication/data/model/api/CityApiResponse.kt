package com.example.weatherapplication.data.model.api

import com.google.gson.annotations.SerializedName

/**
 * Api response when searching cities
 */
data class CityApiResponse(

    @field:SerializedName("country")
    val country: String? = "",

    @field:SerializedName("name")
    val name: String? = "",

    @field:SerializedName("lon")
    val lon: Float? = 0f,

    @field:SerializedName("id")
    val id: Int? = 0,

    @field:SerializedName("region")
    val region: String? = "",

    @field:SerializedName("lat")
    val lat: Float? = 0f,

    @field:SerializedName("url")
    val url: String? = ""
)
