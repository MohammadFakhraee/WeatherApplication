package com.example.weatherapplication.feature.search

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.R
import com.example.weatherapplication.feature.search.uimodel.CitySearchUi
import com.example.weatherapplication.feature.search.uimodel.LocationUi
import com.example.weatherapplication.feature.search.uimodel.SearchLocationUiState
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import com.example.weatherapplication.usecase.city.GetCities
import com.example.weatherapplication.usecase.city.GetCity
import com.example.weatherapplication.util.marker.AppMarker
import com.example.weatherapplication.util.marker.MapMarkerCreator
import com.example.weatherapplication.util.marker.NeshanMapMarkerCreator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * View logic holder of [SearchLocationFragment]. It also implements [MapMarkerCreator.createMarker] which will be
 * responsible of creating [AppMarker] instance to avoid the app depend on Map's own marker.
 * @see [MapMarkerCreator]
 * @see [NeshanMapMarkerCreator]
 */
@HiltViewModel
class SearchLocationViewModel @Inject constructor(private val getCities: GetCities, private val getCity: GetCity) : ViewModel(),
    MapMarkerCreator by NeshanMapMarkerCreator() {

    /**
     * A hot mutable flow which holds current state of the view.
     * @see SearchLocationUiState for different state types
     */
    private val _state: MutableStateFlow<SearchLocationUiState> = MutableStateFlow(SearchLocationUiState.SelectLocationState())

    /**
     * Read-only back property of [_state]
     */
    val state: StateFlow<SearchLocationUiState> = _state

    /**
     * Coroutine Job responsible for 1000 ms delay after text input.
     * @see searchCity
     */
    private var job: Job? = null

    /**
     * Latest user selected marker stores here
     */
    private var userSelectedMarker: AppMarker? = null

    /**
     * Latest current user location stores here
     */
    private var userCurrentLocationMarker: AppMarker? = null

    /**
     * Requests for searching cities having [searchField] in their name.
     * It cancels [job] if already has an active one then creates a coroutine which will have 1000 ms delay before
     * sending request.
     * While typing in edit text we listen to character changes with 'doAfterTextChanged'.
     * It triggers this method everytime a character is added to edit text or removed from it.
     * There is no way that we find out when exactly user's input is being finished, but we can do a trick.
     * We create a coroutine which will have a delay of 1000 ms then we store its job in a variable.
     * If the method gets another call from edit text before running the request it would cancel the job and creates another one...
     * @param searchField city name user wants to search
     */
    fun searchCity(searchField: String) {
        // cancel job if it exists
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            // make a delay of 1000 ms
            delay(1000)
            // user has stopped entering texts, so we check if it is empty or not
            searchField.takeIf { it.isNotEmpty() }?.let {
                // input text is not empty. calling to loading state
                _state.value = SearchLocationUiState.LoadingState
                getCities.run(GetCities.CitySearchRequestValue(searchField)) { response, _ ->
                    when (response) {
                        is UseCaseResponseWrapper.Success -> updateUi(response.result.citiesSearchUi)
                        is UseCaseResponseWrapper.Error -> handleError(response.t)
                    }
                }
            }
        }
    }

    fun <T, K> Flow<T>.map(transform: suspend (value: T) -> K): Flow<K> = flow {
        collect {
            val result = transform(it)
            emit(result)
        }
    }

    /**
     * When user is in searching state and wants to go back,
     * we change the state from search state to select location state
     */
    fun onSearchBoxBackPressed() {
        _state.value = SearchLocationUiState.SelectLocationState()
    }

    /**
     * When user requests for focusing on their current location if available. Otherwise,
     * we send an error state on user location to make turn on GPS request.
     */
    fun onFocusOnUserLocationPressed() {
        userCurrentLocationMarker?.let {
            _state.value = SearchLocationUiState.FocusLocationState(
                LocationUi(null, null, null, it)
            )
        } ?: let {
            _state.value = SearchLocationUiState.UserLocationErrorState
        }
    }

    /**
     * When user is in seeing map state and clicks on SearchBox view,
     * we change the state from seeing a map to see an empty list with a search edit text.
     */
    fun onSearchBoxPressed() {
        _state.value = SearchLocationUiState.SearchState
    }

    /**
     * Selects user clicked location on map by creating a marker using
     * [MapMarkerCreator.createMarker] and updating view
     * @param latitude of user's selected location
     * @param longitude of user's selected location
     * @param markerIcon bitmap used to show user selected location on map
     */
    fun onMapLongClick(latitude: Double, longitude: Double, markerIcon: Bitmap) {
        // create a marker
        val appMarker = createMarker(latitude, longitude, markerIcon)
        // update select location state
        _state.value = SearchLocationUiState.SelectLocationState(
            LocationUi(userSelectedMarker, appMarker, null, null)
        )
        // store latest user's selected location
        userSelectedMarker = appMarker
    }

    /**
     * Updates user's current location by taking lat & lng of the user and creating a marker with
     * [MapMarkerCreator.createMarker] then if the user is in select location state then it will update the state
     * @param latitude of user's current location
     * @param longitude of user's current location
     * @param markerIcon bitmap used to show user current location on map
     */
    fun onUserLocationUpdate(latitude: Double, longitude: Double, markerIcon: Bitmap) {
        // create a marker
        val appMarker = createMarker(latitude, longitude, markerIcon)
        _state.value
            // check if user is in select location state
            .takeIf { isInSelectLocationState() }
            ?.let {
                // user is in select location state so we update its state.
                _state.value = SearchLocationUiState.SelectLocationState(
                    LocationUi(null, null, userCurrentLocationMarker, appMarker)
                )
            }
        // store latest user location
        userCurrentLocationMarker = appMarker
    }

    /**
     * When user chooses a city from searched list, it will be called.
     * We request for location full name because we don't have the name inside [CitySearchUi], but its [CitySearchUi.id]
     */
    fun onCitySearchSelected(citySearchUi: CitySearchUi) {
        viewModelScope.launch(Dispatchers.IO) {
            getCity.run(GetCity.CityRequestValues(citySearchUi.id)) { response, _ ->
                when (response) {
                    is UseCaseResponseWrapper.Success -> _state.value = SearchLocationUiState.SelectState(response.result.cityName)
                    is UseCaseResponseWrapper.Error -> _state.value = SearchLocationUiState.SelectLocationState()
                }
            }
        }
    }

    /**
     * When user chooses c location from map it will be called.
     */
    fun onLocationSelected(latitude: Double, longitude: Double) {
        _state.value = SearchLocationUiState.LocationSelectState(latitude, longitude)
    }

    /**
     * Will get called whenever a response comes from UseCase.
     */
    private fun updateUi(citiesSearchUi: List<CitySearchUi>) {
        _state.value =
            if (citiesSearchUi.isEmpty()) SearchLocationUiState.EmptyState(R.string.nothing_found)
            else SearchLocationUiState.DataState(citiesSearchUi)
    }

    /**
     * Error handling happens here..
     */
    private fun handleError(t: Throwable) {
        _state.value = when (t) {
            is IOException -> SearchLocationUiState.ErrorState(R.string.error_io_exception)
            else -> SearchLocationUiState.ErrorState(R.string.error_server_exception)
        }
    }

    /**
     * Checks if user is in select location state
     */
    private fun isInSelectLocationState(): Boolean =
        _state.value.let {
            it is SearchLocationUiState.SelectLocationState
                    || it is SearchLocationUiState.FocusLocationState
                    || it is SearchLocationUiState.UserLocationErrorState
        }
}