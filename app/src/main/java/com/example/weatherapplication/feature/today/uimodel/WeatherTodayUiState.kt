package com.example.weatherapplication.feature.today.uimodel

import androidx.annotation.StringRes
import com.example.weatherapplication.feature.today.WeatherTodayFragment
import com.example.weatherapplication.feature.today.WeatherTodayViewModel

/**
 * Different states happening in [WeatherTodayFragment].
 * [WeatherTodayViewModel] will trigger one of the below states based on the user interactions.
 */
sealed interface WeatherTodayUiState {
    data class StillLoadingDataState(val weatherTodayUi: WeatherTodayUi) : WeatherTodayUiState

    data class LoadCompleteDataState(val weatherTodayUi: WeatherTodayUi) : WeatherTodayUiState

    data class ErrorState(@StringRes val errorTxtId: Int) : WeatherTodayUiState

    data class LoadingState(val dataLoaded: Boolean) : WeatherTodayUiState

    object EmptyLocationState: WeatherTodayUiState
}