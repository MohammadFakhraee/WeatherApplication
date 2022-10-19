package com.example.weatherapplication.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Base application class of the entire app.
 * It implements the worker [Configuration.Provider] methods in order to DI in worker classes
 */
@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    /**
     * Instance of worker factory. In order to dependency injection inside worker classes, we need to  have this class here
     * @see getWorkManagerConfiguration
     */
    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    /**
     * Builds an instance of worker configuration which takes [hiltWorkerFactory]
     * as worker factory.
     */
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setWorkerFactory(hiltWorkerFactory).build()

    /**
     * In devices having api >=26, we have to create a notification channel if we need to notify user.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG, "----- createChannel -----")
            val channel = NotificationChannel(
                CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = NOTIFICATION_CHANNEL_DESCRIPTION }

            // Get notificationManager from context
            val notificationManager = this
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            // Add channel if there isn't one.
            notificationManager
                ?.takeIf {
                    !it.notificationChannels.contains(channel)
                }?.let {
                    Log.i(TAG, "createChannel: didn't contain channel")
                    it.createNotificationChannel(channel)
                }
        }
    }

    companion object {
        // Notification channel constants
        const val NOTIFICATION_CHANNEL_NAME: String = "My app notifications"
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Shows a notification of plugged in counts"
        const val CHANNEL_ID = "channel_1001"

        const val TAG = "notification_" +
                "tag"
    }
}