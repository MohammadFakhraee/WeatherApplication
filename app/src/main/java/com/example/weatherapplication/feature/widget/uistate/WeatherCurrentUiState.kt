package com.example.weatherapplication.feature.widget.uistate

import androidx.annotation.StringRes

sealed interface WeatherCurrentUiState {

    data class ErrorState(@StringRes val messageId: Int): WeatherCurrentUiState

    data class DataState(val weatherCurrentUi: WeatherCurrentUi = WeatherCurrentUi()) : WeatherCurrentUiState

}