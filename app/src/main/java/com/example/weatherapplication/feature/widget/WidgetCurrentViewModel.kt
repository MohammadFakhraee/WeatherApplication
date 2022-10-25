package com.example.weatherapplication.feature.widget

import com.example.weatherapplication.R
import com.example.weatherapplication.core.pref.ApplicationSharedPrefManager
import com.example.weatherapplication.feature.widget.uistate.WidgetWeatherCurrentUi
import com.example.weatherapplication.feature.widget.uistate.WidgetWeatherCurrentUiState
import com.example.weatherapplication.usecase.weather.GetCurrentByCity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class WidgetCurrentViewModel @Inject constructor(
    private val getCurrentByCity: GetCurrentByCity,
    private val applicationSharedPrefManager: ApplicationSharedPrefManager
) {
    private val _state: MutableStateFlow<WidgetWeatherCurrentUiState> = MutableStateFlow(WidgetWeatherCurrentUiState.DataStateWidget())

    val state: StateFlow<WidgetWeatherCurrentUiState> = _state

    // Loading current weather information from city name stored in shared preferences
    fun loadCurrentData() {
        CoroutineScope(Dispatchers.IO).launch {
            applicationSharedPrefManager.lastLocationLoadedName.takeIf { it.isNotEmpty() }
                ?.let { cityName ->
                    getCurrentByCity.run(GetCurrentByCity.ByCityReqVal(cityName))
                        .catch { exception -> handleError(exception) }
                        .collect { response -> updateUi(response.widgetWeatherCurrentUi) }
                }
        }
    }

    private fun updateUi(widgetWeatherCurrentUi: WidgetWeatherCurrentUi) {
        _state.value = WidgetWeatherCurrentUiState.DataStateWidget(widgetWeatherCurrentUi)
    }

    private fun handleError(t: Throwable) {
        _state.value = when (t) {
            is IOException -> WidgetWeatherCurrentUiState.ErrorStateWidget(R.string.error_io_exception)
            else -> WidgetWeatherCurrentUiState.ErrorStateWidget(R.string.error_server_exception)
        }
    }
}