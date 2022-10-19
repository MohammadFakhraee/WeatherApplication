package com.example.weatherapplication.feature.today.uimodel

import com.example.weatherapplication.feature.today.WeatherTodayFragment
import com.example.weatherapplication.feature.today.WeatherTodayViewModel

/**
 * Different WORKER states happening in [WeatherTodayFragment].
 * [WeatherTodayViewModel] will trigger one of the below states based on the user interactions.
 */
sealed interface WorkerConfigState {
    object Default: WorkerConfigState
    data class DataHolder(val workerConfig: WorkerConfig): WorkerConfigState
}