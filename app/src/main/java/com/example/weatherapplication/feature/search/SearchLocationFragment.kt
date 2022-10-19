package com.example.weatherapplication.feature.search

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weatherapplication.R
import com.example.weatherapplication.core.base.BaseFragment
import com.example.weatherapplication.databinding.FragmentSearchLocationBinding
import com.example.weatherapplication.feature.search.adapter.CitySearchListAdapter
import com.example.weatherapplication.feature.search.uimodel.CitySearchUi
import com.example.weatherapplication.feature.search.uimodel.LocationUi
import com.example.weatherapplication.feature.search.uimodel.SearchLocationUiState
import com.example.weatherapplication.feature.shared.TodaySearchSharedViewModel
import com.example.weatherapplication.util.PermissionManager
import com.example.weatherapplication.util.UserLocationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchLocationFragment : BaseFragment<FragmentSearchLocationBinding>() {
    private val searchLocationViewModel: SearchLocationViewModel by viewModels()

    // To update user's selected location or city
    private val todaySearchSharedViewModel: TodaySearchSharedViewModel by activityViewModels()

    @Inject
    lateinit var citySearchListAdapter: CitySearchListAdapter // Shows a list of searched cities

    /**
     * User's location manager which will be updated periodically.
     * Since user's location logic completely depends on fragment and context, I thought it would be better to
     * have this manager inside fragment instead of view model.
     * @see UserLocationManager
     */
    private val userLocationManager = UserLocationManager(this, object : PermissionManager.PermissionCallback {
        override fun onPermissionGranted() {
            Toast.makeText(requireContext(), getString(R.string.user_permission_granted), Toast.LENGTH_LONG).show()
        }

        override fun onPermissionDenied() {
            Toast.makeText(requireContext(), getString(R.string.user_permission_denied, "Location"), Toast.LENGTH_LONG).show()
        }
    })

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSearchLocationBinding =
        FragmentSearchLocationBinding.inflate(inflater, container, false)

    @SuppressLint("ClickableViewAccessibility")
    override fun onInitBindingCallback() {
        requireBinding {
            neshanMapView.setOnMapLongClickListener { chosenLocation ->
                val bitmap: Bitmap = BitmapFactory.decodeResource(requireActivity().applicationContext.resources, R.drawable.location_marker)
                searchLocationViewModel.onMapLongClick(chosenLocation.latitude, chosenLocation.longitude, bitmap)
            }

            neshanMapView.setOnMarkerClickListener { searchLocationViewModel.onLocationSelected(it.latLng.latitude, it.latLng.longitude) }

            searchBoxCv.setOnClickListener { searchLocationViewModel.onSearchBoxPressed() }

            searchCityEt.doAfterTextChanged { editable ->
                searchLocationViewModel.searchCity(editable.toString())
            }

            searchCityRv.adapter = citySearchListAdapter

            backIb.setOnClickListener { searchLocationViewModel.onSearchBoxBackPressed() }

            myLocationFab.setOnClickListener {
                Log.i("UserLocationFocus", "onInitBindingCallback: focus on user location is touched")
                searchLocationViewModel.onFocusOnUserLocationPressed()
            }
        }

        citySearchListAdapter.onItemClickListener = { searchLocationViewModel.onCitySearchSelected(it) }

        lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Creates a coroutine to collect ui state changes from SearchLocationViewModel
                launch {
                    searchLocationViewModel.state.collect { uiState ->
                        when (uiState) {
                            is SearchLocationUiState.SelectLocationState -> onDefault(uiState.locationUi)
                            is SearchLocationUiState.SearchState -> onSearch()
                            is SearchLocationUiState.LoadingState -> onLoading()
                            is SearchLocationUiState.ErrorState -> onError(uiState.errorTxtId)
                            is SearchLocationUiState.DataState -> onData(uiState.citiesSearchUi)
                            is SearchLocationUiState.EmptyState -> onEmptyList(uiState.emptyTxtId)
                            is SearchLocationUiState.SelectState -> onCitySelect(uiState.cityName)
                            is SearchLocationUiState.LocationSelectState -> onLocationSelect(uiState.latitude, uiState.longitude)
                            is SearchLocationUiState.FocusLocationState -> onFocusOnUserLocation(uiState.locationUi)
                            is SearchLocationUiState.UserLocationErrorState -> onUserLocationError()
                        }
                    }
                }

                // Creates a coroutine to collect user location updates from UserLocationManager class
                launch {
                    userLocationManager.lastLocationStateFlow.collect { userLocation ->
                        userLocation?.let {
                            Log.i(
                                "USerLocationManager",
                                "onInitBindingCallback: location is get correctly with data: Latitude = ${userLocation.latitude}, Longitude = ${userLocation.longitude}"
                            )
                            val bitmap: Bitmap =
                                BitmapFactory.decodeResource(requireActivity().applicationContext.resources, R.drawable.ic_user_location)
                            searchLocationViewModel.onUserLocationUpdate(userLocation.latitude, userLocation.longitude, bitmap)
                        }
                    }
                }
            }
        }
    }

    // When user has clicked to focus their current location BUT their gps is off so we request to start location updates
    private fun onUserLocationError() {
        userLocationManager.startLocationUpdates()
    }

    // When user has clicked on focus their location AND their gps is on so we can add their location's marker on the map
    private fun onFocusOnUserLocation(locationUi: LocationUi) {
        locationUi.newUserMarker?.marker?.let { userLocation ->
            requireBinding {
                neshanMapView.moveCamera(userLocation.latLng, 0.25f)
                neshanMapView.setZoom(15f, 0.25f)
            }
        }
    }

    // Default state of user which the map and search box are shown and markers are updated
    private fun onDefault(locationUi: LocationUi) {
        requireBinding {
            searchConstraintLayout.visibility = GONE
            neshanMapView.visibility = VISIBLE
            searchBoxCv.visibility = VISIBLE
            myLocationFab.visibility = VISIBLE
            locationUi.prevSelectedMarker?.let { neshanMapView.removeMarker(it.marker) }
            locationUi.newSelectedMarker?.let { neshanMapView.addMarker(it.marker) }
            locationUi.prevUserMarker?.let { neshanMapView.removeMarker(it.marker) }
            locationUi.newUserMarker?.let { neshanMapView.addMarker(it.marker) }
        }
    }

    // When user has touched the search box and wants to search a city name
    private fun onSearch() {
        requireBinding {
            searchConstraintLayout.visibility = VISIBLE
            myLocationFab.visibility = GONE
            neshanMapView.visibility = GONE
            searchBoxCv.visibility = GONE
            searchCityEt.isEnabled = true
            progressBar.visibility = GONE
            searchCityRv.visibility = GONE
            emptyStateIv.visibility = GONE
            errorTv.visibility = GONE
        }
    }

    // When user has typed a city name to search and data is being loaded
    private fun onLoading() {
        requireBinding {
            searchConstraintLayout.visibility = VISIBLE
            myLocationFab.visibility = GONE
            neshanMapView.visibility = GONE
            searchBoxCv.visibility = GONE
            searchCityEt.isEnabled = false
            progressBar.visibility = VISIBLE
            searchCityRv.visibility = GONE
            emptyStateIv.visibility = GONE
            errorTv.visibility = GONE
        }
    }

    // When city search result has been failed
    private fun onError(@StringRes errorTxtId: Int) {
        requireBinding {
            searchConstraintLayout.visibility = VISIBLE
            neshanMapView.visibility = GONE
            myLocationFab.visibility = GONE
            searchBoxCv.visibility = GONE
            searchCityEt.isEnabled = true
            progressBar.visibility = GONE
            searchCityRv.visibility = GONE
            emptyStateIv.visibility = VISIBLE
            errorTv.visibility = VISIBLE
            errorTv.text = getString(errorTxtId)
        }
    }

    // When city search result was empty
    private fun onEmptyList(@StringRes emptyTxtId: Int) {
        requireBinding {
            searchConstraintLayout.visibility = VISIBLE
            neshanMapView.visibility = GONE
            myLocationFab.visibility = GONE
            searchBoxCv.visibility = GONE
            searchCityEt.isEnabled = true
            progressBar.visibility = GONE
            searchCityRv.visibility = GONE
            emptyStateIv.visibility = VISIBLE
            errorTv.visibility = VISIBLE
            errorTv.text = getString(emptyTxtId)
        }
    }

    // When city search result was not empty and was completed successfully
    private fun onData(citiesSearchUi: List<CitySearchUi>) {
        requireBinding {
            searchConstraintLayout.visibility = VISIBLE
            neshanMapView.visibility = GONE
            myLocationFab.visibility = GONE
            searchBoxCv.visibility = GONE
            searchCityEt.isEnabled = true
            progressBar.visibility = GONE
            searchCityRv.visibility = VISIBLE
            emptyStateIv.visibility = GONE
            errorTv.visibility = GONE
        }
        citySearchListAdapter.submitList(citiesSearchUi)
        Toast.makeText(requireContext(), "Found ${citiesSearchUi.size} results.", Toast.LENGTH_LONG).show()
    }

    /**
     * When user selects a city name to update weather information
     * It calls [TodaySearchSharedViewModel] to update the state
     */
    private fun onCitySelect(cityUrl: String) {
        todaySearchSharedViewModel.onCitySelected(cityUrl)
        findNavController().popBackStack()
    }

    /**
     * When user selects a location to update weather information
     * It calls [TodaySearchSharedViewModel] to update the state
     */
    private fun onLocationSelect(latitude: Double, longitude: Double) {
        todaySearchSharedViewModel.onLocationSelected(latitude, longitude)
        findNavController().popBackStack()
    }
}