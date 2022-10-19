package com.example.weatherapplication.feature.shared

import androidx.lifecycle.ViewModel
import com.example.weatherapplication.feature.search.SearchLocationFragment
import com.example.weatherapplication.feature.today.WeatherTodayFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Shared view model between [SearchLocationFragment] and [WeatherTodayFragment].
 * [SearchLocationFragment] will call [onLocationSelected] and [onCitySelected] methods.
 * [WeatherTodayFragment] will observe [locationStateFlow] and [searchFieldStateFlow] state flows.
 */
@HiltViewModel
class TodaySearchSharedViewModel @Inject constructor() : ViewModel() {

    /**
     * It will get a lat & lng doubles as a Pair inputs
     */
    private val _locationStateFlow: MutableStateFlow<Pair<Double, Double>?> = MutableStateFlow(null)

    val locationStateFlow: StateFlow<Pair<Double, Double>?> = _locationStateFlow

    /**
     * It will get a string city name as input
     */
    private val _searchFieldStateFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    val searchFieldStateFlow: StateFlow<String?> = _searchFieldStateFlow

    fun onLocationSelected(latitude: Double, longitude: Double) {
        _searchFieldStateFlow.value = null
        _locationStateFlow.value = Pair(latitude, longitude)
    }

    fun onCitySelected(url: String) {
        _searchFieldStateFlow.value = url
        _locationStateFlow.value = null
    }
}