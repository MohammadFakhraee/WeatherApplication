package com.example.weatherapplication.feature.search.uimodel

import com.example.weatherapplication.util.marker.AppMarker

/**
 * UI data model used in view layer
 * @param prevSelectedMarker previously selected marker on the map
 * @param newSelectedMarker newly selected marker on the map
 * @param prevUserMarker previously user location
 * @param newUserMarker newly user location
 */
data class LocationUi(
    var prevSelectedMarker: AppMarker? = null,
    var newSelectedMarker: AppMarker? = null,
    var prevUserMarker: AppMarker? = null,
    var newUserMarker: AppMarker? = null
)