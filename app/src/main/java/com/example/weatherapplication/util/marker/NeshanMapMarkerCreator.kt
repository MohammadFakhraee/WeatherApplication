package com.example.weatherapplication.util.marker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.carto.styles.AnimationStyleBuilder
import com.carto.styles.AnimationType
import com.carto.styles.MarkerStyleBuilder
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.internal.utils.BitmapUtils
import org.neshan.mapsdk.model.Marker
import javax.inject.Inject

class NeshanMapMarkerCreator @Inject constructor(): MapMarkerCreator  {

    override fun createMarker(latitude: Double, longitude: Double, markerIcon: Bitmap): AppMarker {
        // Initially creating AnimationStyle for marker. We use AnimationStyleBuilder
        // to set initial values and then call its buildStyle() function to create the animation
        val animStBl = AnimationStyleBuilder()
        animStBl.fadeAnimationType = AnimationType.ANIMATION_TYPE_SMOOTHSTEP
        animStBl.sizeAnimationType = AnimationType.ANIMATION_TYPE_STEP
        animStBl.phaseInDuration = 0.5f
        animStBl.phaseOutDuration = 0.5f
        //Secondly creating MarkerStyle using its Builder.
        val markerStBl = MarkerStyleBuilder()
        markerStBl.size = 30f
        markerStBl.bitmap = BitmapUtils.createBitmapFromAndroidBitmap(markerIcon)
        markerStBl.animationStyle = animStBl.buildStyle()
        //Finally creating marker itself
        return AppMarker(Marker(LatLng(latitude, longitude), markerStBl.buildStyle()))
    }


}