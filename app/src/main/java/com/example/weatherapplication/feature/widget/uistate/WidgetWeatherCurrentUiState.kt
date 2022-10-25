package com.example.weatherapplication.feature.widget.uistate

import androidx.annotation.StringRes

sealed interface WidgetWeatherCurrentUiState {

    data class ErrorStateWidget(@StringRes val messageId: Int): WidgetWeatherCurrentUiState

    data class DataStateWidget(val widgetWeatherCurrentUi: WidgetWeatherCurrentUi = WidgetWeatherCurrentUi()) : WidgetWeatherCurrentUiState

}