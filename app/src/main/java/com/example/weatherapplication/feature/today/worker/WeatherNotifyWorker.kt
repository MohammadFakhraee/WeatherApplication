package com.example.weatherapplication.feature.today.worker

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapplication.R
import com.example.weatherapplication.core.MyApp
import com.example.weatherapplication.core.pref.ApplicationSharedPrefManager
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.usecase.weather.GetForecastByCity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@HiltWorker
class WeatherNotifyWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getForecastByCity: GetForecastByCity,
    private val applicationSharedPrefManager: ApplicationSharedPrefManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result =
        coroutineScope {
            // starting a coroutine which will request for weather update
            launch(Dispatchers.IO) {
                Log.i("WorkerManagingFlow", "doWork: Worker has been started successfully.")
                // Seeing if user already chose a location or not
                applicationSharedPrefManager.lastLocationLoadedName.takeIf { it.isNotEmpty() }?.let { cityName ->
                    // Already have chosen a city
                    // Getting weather info for the city
                    getForecastByCity.run(
                        GetForecastByCity.ByCityReqVal(cityName, 7, aqr = false, alerts = false)
                    ).collect { response -> onWeatherResponse(response.weatherTodayUi) }
                }
            }
            // Then finishing worker with success
            Result.success()
        }

    private fun onWeatherResponse(weatherTodayUi: WeatherTodayUi) {
        val cityName = weatherTodayUi.currentWeatherUi.locationName
        val temp = weatherTodayUi.currentWeatherUi.tempC
        val notificationTitle = applicationContext.getString(R.string.notification_title, cityName, temp)
        val notificationDesc = weatherTodayUi.currentWeatherUi.conditionText
        // Triggering notification
        triggerNotification(notificationTitle, notificationDesc)
    }

    private fun triggerNotification(notificationTitle: String, notificationDesc: String) {
        val notification = NotificationCompat.Builder(applicationContext, MyApp.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDesc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(101, notification)
    }
}