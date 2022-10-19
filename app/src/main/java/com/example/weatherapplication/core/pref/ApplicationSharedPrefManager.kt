package com.example.weatherapplication.core.pref

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Application's shared preference manager
 */
class ApplicationSharedPrefManager @Inject constructor(private val sharedPref: SharedPreferences) {

    /**
     * The name of the latest loaded location will store and retrieve with this variable.
     * It will be used:
     * 1. when the application is in offline mode,
     * 2. updating application widget,
     * 3. informing user periodically using notification.
     */
    var lastLocationLoadedName: String
        set(value) = sharedPref.edit().putString(KEY_LAST_LOCATION_NAME, value).apply()
        get() = sharedPref.getString(KEY_LAST_LOCATION_NAME, "") ?: ""

    /**
     * Stores and returns the value of weather retriever worker.
     * Returns true - which means the weather worker has been set properly.
     * Returns false - which means the weather worker has not been set yet.
     */
    var setupWeatherRetrieverWorker: Boolean
        set(value) = sharedPref.edit().putBoolean(KEY_SETUP_WEATHER_RETRIEVER_WORKER, value).apply()
        get() = sharedPref.getBoolean(KEY_SETUP_WEATHER_RETRIEVER_WORKER, false)

    companion object {
        const val KEY_LAST_LOCATION_NAME = "last_location_name"
        const val KEY_SETUP_WEATHER_RETRIEVER_WORKER = "setup_weather_retriever_worker"
    }
}