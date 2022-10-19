package com.example.weatherapplication.feature.search.uimodel

import androidx.annotation.StringRes
import com.example.weatherapplication.feature.search.SearchLocationFragment
import com.example.weatherapplication.feature.search.SearchLocationViewModel

/**
 * Different states happening in [SearchLocationFragment].
 * [SearchLocationViewModel] will trigger one of the below states based on the user interactions.
 */
sealed interface SearchLocationUiState {
    data class SelectLocationState(val locationUi: LocationUi = LocationUi()) : SearchLocationUiState

    data class FocusLocationState(val locationUi: LocationUi) : SearchLocationUiState

    object SearchState : SearchLocationUiState

    object UserLocationErrorState : SearchLocationUiState

    object LoadingState : SearchLocationUiState

    data class EmptyState(@StringRes val emptyTxtId: Int) : SearchLocationUiState

    data class ErrorState(@StringRes val errorTxtId: Int) : SearchLocationUiState

    data class DataState(val citiesSearchUi: List<CitySearchUi>) : SearchLocationUiState

    data class SelectState(val cityName: String) : SearchLocationUiState

    data class LocationSelectState(val latitude: Double, val longitude: Double) : SearchLocationUiState
}