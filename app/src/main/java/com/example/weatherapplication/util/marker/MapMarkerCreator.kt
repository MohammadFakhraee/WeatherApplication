package com.example.weatherapplication.util.marker

import android.graphics.Bitmap
import org.neshan.mapsdk.model.Marker

/**
 * we should specify [MapMarkerCreator]'s type inside [com.example.weatherapplication.feature.search.SearchLocationViewModel]
 * and [AppMarker]'s type input. And that would make us change type input in every class
 * it's used in, if we wanted to change our map API.
 * But with current implementation we would just change the type of marker stored inside [AppMarker] and it would do the trick.
 */
interface MapMarkerCreator {
    /**
     * Gets a location as an input and creates marker with an input icon on that location
     * @param latitude latitude of marker location
     * @param longitude longitude of marker location
     * @param markerIcon a bitmap that is used for drawing marker
     */
    fun createMarker(latitude: Double, longitude: Double, markerIcon: Bitmap): AppMarker
}

/**
 * To independent the application from Map's own marker
 */
data class AppMarker(val marker: Marker)



