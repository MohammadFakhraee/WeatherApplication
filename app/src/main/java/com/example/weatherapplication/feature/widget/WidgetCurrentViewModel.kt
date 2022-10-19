package com.example.weatherapplication.feature.widget

import com.example.weatherapplication.R
import com.example.weatherapplication.core.pref.ApplicationSharedPrefManager
import com.example.weatherapplication.feature.widget.uistate.WeatherCurrentUi
import com.example.weatherapplication.feature.widget.uistate.WeatherCurrentUiState
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import com.example.weatherapplication.usecase.weather.GetCurrentByCity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class WidgetCurrentViewModel @Inject constructor(
    private val getCurrentByCity: GetCurrentByCity,
    private val applicationSharedPrefManager: ApplicationSharedPrefManager
) {
    private val _state: MutableStateFlow<WeatherCurrentUiState> = MutableStateFlow(WeatherCurrentUiState.DataState())

    val state: StateFlow<WeatherCurrentUiState> = _state

    // Loading current weather information from city name stored in shared preferences
    fun loadCurrentData() {
        CoroutineScope(Dispatchers.IO).launch {
            applicationSharedPrefManager.lastLocationLoadedName.takeIf { it.isNotEmpty() }
                ?.let { cityName ->
                    getCurrentByCity.run(GetCurrentByCity.ByCityReqVal(cityName)) { response, stillLoading ->
                        if (!stillLoading)
                            when (response) {
                                is UseCaseResponseWrapper.Success -> updateUi(response.result.weatherCurrentUi)
                                is UseCaseResponseWrapper.Error -> handleError(response.t)
                            }
                    }
                }
        }
    }

    private fun updateUi(weatherCurrentUi: WeatherCurrentUi) {
        _state.value = WeatherCurrentUiState.DataState(weatherCurrentUi)
    }

    private fun handleError(t: Throwable) {
        _state.value = when (t) {
            is IOException -> WeatherCurrentUiState.ErrorState(R.string.error_io_exception)
            else -> WeatherCurrentUiState.ErrorState(R.string.error_server_exception)
        }
    }
}