package com.example.weatherapplication.feature.today

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.R
import com.example.weatherapplication.core.pref.ApplicationSharedPrefManager
import com.example.weatherapplication.feature.search.SearchLocationFragment
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUiState
import com.example.weatherapplication.feature.today.uimodel.WorkerConfig
import com.example.weatherapplication.feature.today.uimodel.WorkerConfigState
import com.example.weatherapplication.feature.today.worker.WeatherNotifyWorker
import com.example.weatherapplication.usecase.UseCaseResponseWrapper
import com.example.weatherapplication.usecase.weather.GetForecastByCity
import com.example.weatherapplication.usecase.weather.GetForecastByGeo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * Responsible view model for [WeatherTodayFragment]
 * @param getForecastByCity use case to get weather information with city name
 * @param getForecastByGeo use case to get weather information with geographic coordinates
 * @param applicationSharedPrefManager to obtain initial values for [cityName] and [_routineWorkerState]
 */
@HiltViewModel
class WeatherTodayViewModel @Inject constructor(
    private val getForecastByCity: GetForecastByCity,
    private val getForecastByGeo: GetForecastByGeo,
    private val applicationSharedPrefManager: ApplicationSharedPrefManager
) : ViewModel() {
    // Mutable property for holding current user state
    // Private object which is encapsulated from other classes to change the value
    private val _state: MutableStateFlow<WeatherTodayUiState> = MutableStateFlow(WeatherTodayUiState.LoadingState(false))

    // Back property of _state. StateFlow for classes to listen for state changes
    val state: StateFlow<WeatherTodayUiState> = _state

    /**
     * Mutable property for holding daily routine worker.
     * Private object which is encapsulated from other classes.
     * Notifies its fragment to tell whether worker has been set properly or not. If not
     * fragment should create a [WeatherNotifyWorker] with values described in [WorkerConfig].
     * @see WeatherNotifyWorker to find out what the worker does
     * @see WorkerConfig for worker initial properties
     */
    private val _routineWorkerState: MutableStateFlow<WorkerConfigState> = MutableStateFlow(WorkerConfigState.Default)

    // Back property for _routineWorkerState
    val routineWorkerState: StateFlow<WorkerConfigState> = _routineWorkerState

    // Stores whether the data has been loaded properly or not
    private var isDataLoaded: Boolean = false

    // City name will be saved here if user had chosen city to check weather
    private var cityName: String? = null

    // A location will be saved here if user had chosen a location to check weather
    private var latLng: Pair<Double, Double>? = null

    // Number of times user has been navigated to current fragment.
    // This will be used to ask user for notification permission on android api 33+
    private var viewLaunchTimes: Int = 0

    fun onInit() {
        _routineWorkerState.value = WorkerConfigState.Default
        // Checking if the the device sdk int is equal or above 33, every 5 times that the current fragment is being loaded
        // Because if the user's device uses android API 33+, it will be bothering them if the app shows notification permission every single time.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && viewLaunchTimes++ % 5 == 0)
            requestForWorker()
        else requestForWorker()

        applicationSharedPrefManager
            .lastLocationLoadedName
            .takeIf { it.isNotEmpty() }
            ?.let { onLoadWithCityName(it) }
            ?: handleSharedPrefEmptyCityName()
    }

    private fun requestForWorker() {
        // Checking if the the worker has been set in shared preferences. If not, it will change the state of
        // _routineWorkerState
        if (!applicationSharedPrefManager.setupWeatherRetrieverWorker) {
            Log.i("WorkerManagingFlow", "onInit: Worker has not been set. Creating calendars...")
            val currentCal = Calendar.getInstance()
            val firstWorkCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, WORKER_START_HOUR)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (before(currentCal)) add(Calendar.DAY_OF_MONTH, 1)
            }
            val initialDelay = firstWorkCal.timeInMillis - currentCal.timeInMillis
            Log.i("WorkerManagingFlow", "requestForWorker: FirstWorkerCall time is: ${firstWorkCal.time}")
            // Creating worker config to set initialDelay & intervalHours
            val workerConfig = WorkerConfig(initialDelay = initialDelay, intervalHours = WORKER_TIME_INTERVAL)
            Log.i("WorkerManagingFlow", "requestForWorker: Worker configs are $workerConfig")
            // Changing worker config state flow
            _routineWorkerState.value = WorkerConfigState.DataHolder(workerConfig)
        }
    }

    // When user hasn't already chosen a city to check weather information this will change the state
    // to make fragment navigating to search fragment
    private fun handleSharedPrefEmptyCityName() {
        _state.value = WeatherTodayUiState.EmptyLocationState
    }

    // After worker enqueued in fragment, view model changes initial data in shared pref
    fun onWorkerSet() {
        applicationSharedPrefManager.setupWeatherRetrieverWorker = true
        _routineWorkerState.value = WorkerConfigState.Default
    }

    // When fragment is in Error state and user clicks retry button
    fun onRetryClick() {
        cityName?.let { onLoadWithCityName(it) }
        latLng?.let { onLoadWithGeo(it) }
    }

    /**
     * This method is being called in 3 situations:
     * 1. When user chooses a city name while interacting with [SearchLocationFragment].
     * 2. When user navigates to this page and view model checks if there is a city name previously saved in shared pref
     * 3. When user is in error state and touches retry button and [cityName] is already stored.
     */
    fun onLoadWithCityName(cityName: String) {
        this.cityName = cityName
        latLng = null
        loading()
        viewModelScope.launch(Dispatchers.IO) {
            getForecastByCity.run(GetForecastByCity.ByCityReqVal(cityName, 7, aqr = false, alerts = false)) { response, stillLoading ->
                when (response) {
                    is UseCaseResponseWrapper.Success<GetForecastByCity.ByCityResponseVal> ->
                        updateData(response.result.weatherTodayUi, stillLoading)
                    is UseCaseResponseWrapper.Error -> handleError(response.t, stillLoading)
                }
            }
        }
    }

    /**
     * This method is being called in 2 situations:
     * 1. When user chooses a location coordinates while interacting with [SearchLocationFragment].
     * 2. When user is in error state and touches retry button and [latLng] is already stored.
     */
    fun onLoadWithGeo(latLng: Pair<Double, Double>) {
        this.latLng = latLng
        cityName = null
        loading()
        viewModelScope.launch(Dispatchers.IO) {
            getForecastByGeo.run(
                GetForecastByGeo.ByGeoReqVal(
                    latLng.first,
                    latLng.second,
                    7,
                    aqi = false,
                    alerts = true
                )
            ) { response, stillLoading ->
                when (response) {
                    is UseCaseResponseWrapper.Success<GetForecastByGeo.ByGeoResponseVal> ->
                        updateData(response.result.weatherTodayUi, stillLoading)
                    is UseCaseResponseWrapper.Error -> handleError(response.t, stillLoading)
                }
            }
        }
    }

    // Changes state of fragment to loading state
    private fun loading() {
        _state.value = WeatherTodayUiState.LoadingState(isDataLoaded)
    }

    /**
     * Changes state of fragment to data state. [WeatherTodayUiState.StillLoadingDataState] if [stillLoading] is true
     * and [WeatherTodayUiState.LoadCompleteDataState] if not.
     */
    private fun updateData(weatherTodayUi: WeatherTodayUi, stillLoading: Boolean) {
        // setting data is loaded
        isDataLoaded = true
        _state.value =
            if (stillLoading) WeatherTodayUiState.StillLoadingDataState(weatherTodayUi)
            else WeatherTodayUiState.LoadCompleteDataState(weatherTodayUi)
    }

    /**
     * Handles error thrown from data sources.
     * @param t error throwable
     * @param stillLoading whether data is still being loaded in data source layer
     */
    private fun handleError(t: Throwable, stillLoading: Boolean) {
        t.printStackTrace()
        // If data is still loading, wait for the final result
        if (!stillLoading) {
            // We are sure there aren't any active data loader so we can handle error
            // Check if data has been loaded before or not
            if (!isDataLoaded) {
                // No data was loaded before. So we can update fragment state to show error message
                _state.value = when (t) {
                    is IOException -> WeatherTodayUiState.ErrorState(R.string.error_io_exception)
                    else -> WeatherTodayUiState.ErrorState(R.string.error_server_exception)
                }
            } else if (_state.value is WeatherTodayUiState.StillLoadingDataState) {
                // Fragment is StillLoadingState, data is not loading & and no data was loaded before.
                // So we should change the state to the LoadCompleteDataState
                (_state.value as WeatherTodayUiState.StillLoadingDataState).let {
                    _state.value = WeatherTodayUiState.LoadCompleteDataState(it.weatherTodayUi)
                }
            }
        }
    }

    companion object {
        private const val WORKER_TIMES_IN_DAY: Int = 3
        private const val WORKER_TIME_INTERVAL: Long = 24L / WORKER_TIMES_IN_DAY
        private const val WORKER_START_HOUR = 9
    }
}

