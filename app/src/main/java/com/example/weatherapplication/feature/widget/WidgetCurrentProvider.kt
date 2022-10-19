package com.example.weatherapplication.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import androidx.annotation.StringRes
import com.example.weatherapplication.R
import com.example.weatherapplication.core.MainActivity
import com.example.weatherapplication.feature.widget.uistate.WeatherCurrentUi
import com.example.weatherapplication.feature.widget.uistate.WeatherCurrentUiState
import com.example.weatherapplication.util.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WidgetCurrentProvider : AppWidgetProvider() {
    @Inject
    lateinit var widgetCurrentViewModel: WidgetCurrentViewModel

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        widgetCurrentViewModel.loadCurrentData()

        CoroutineScope(Dispatchers.Main).launch {
            widgetCurrentViewModel.state.collect { weatherCurrentUiState ->
                appWidgetIds.forEach { appWidgetId ->
                    val remoteViews: RemoteViews =
                        when (weatherCurrentUiState) {
                            is WeatherCurrentUiState.DataState -> onDataLoaded(context, weatherCurrentUiState.weatherCurrentUi)
                            is WeatherCurrentUiState.ErrorState -> onErrorState(context, weatherCurrentUiState.messageId)
                        }
                    remoteViews.setOnClickPendingIntent(R.id.root, createPendingIntent(context))
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                }
            }
        }
    }

    // Is called when data loaded from server
    private fun onDataLoaded(context: Context, weatherCurrentUi: WeatherCurrentUi): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_weather_current).apply {
            setViewVisibility(R.id.tempLl, VISIBLE)
            setViewVisibility(R.id.locationLl, VISIBLE)
            setViewVisibility(R.id.progressBar, GONE)
            setTextViewText(R.id.currentTempTv, context.getString(R.string.celsius_degree, weatherCurrentUi.currentTempC))
            setTextViewText(R.id.locationTv, weatherCurrentUi.cityName)
            ImageLoader.loadBitmap(context, weatherCurrentUi.iconUrl) { bitmap ->
                setImageViewBitmap(R.id.currentConditionIv, bitmap)
            }
        }
    }

    // Is called when data could not be loaded from server
    private fun onErrorState(context: Context, @StringRes textId: Int): RemoteViews {
        return RemoteViews(context.packageName, R.layout.widget_weather_current).apply {
            setViewVisibility(R.id.tempLl, GONE)
            setViewVisibility(R.id.locationLl, GONE)
            setViewVisibility(R.id.errorTv, VISIBLE)
            setTextViewText(R.id.errorTv, context.getString(textId))
        }
    }

    // Creates a pending intent to open MainActivity on widget click
    private fun createPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}