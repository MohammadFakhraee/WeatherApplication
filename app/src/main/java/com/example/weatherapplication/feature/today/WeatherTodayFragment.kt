package com.example.weatherapplication.feature.today

import android.Manifest
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapplication.R
import com.example.weatherapplication.core.base.BaseFragment
import com.example.weatherapplication.databinding.FragmentWeatherTodayBinding
import com.example.weatherapplication.feature.shared.TodaySearchSharedViewModel
import com.example.weatherapplication.feature.today.adapter.ForecastDaysListAdapter
import com.example.weatherapplication.feature.today.adapter.HourListAdapter
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUi
import com.example.weatherapplication.feature.today.uimodel.WeatherTodayUiState
import com.example.weatherapplication.feature.today.uimodel.WorkerConfig
import com.example.weatherapplication.feature.today.uimodel.WorkerConfigState
import com.example.weatherapplication.feature.today.worker.WeatherNotifyWorker
import com.example.weatherapplication.util.ImageLoader
import com.example.weatherapplication.util.PermissionManager
import com.example.weatherapplication.util.stylishDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class WeatherTodayFragment : BaseFragment<FragmentWeatherTodayBinding>() {
    private val weatherTodayViewModel: WeatherTodayViewModel by viewModels()

    private val todaySearchSharedViewModel: TodaySearchSharedViewModel by activityViewModels()

    @Inject
    lateinit var hourListAdapter: HourListAdapter

    @Inject
    lateinit var forecastDaysListAdapter: ForecastDaysListAdapter

    override fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWeatherTodayBinding =
        FragmentWeatherTodayBinding.inflate(inflater, container, false)

    override fun onInitBindingCallback() {

        weatherTodayViewModel.onInit()

        requireBinding {
            currentLocationTv.setOnClickListener { findNavController().navigate(R.id.action_weatherTodayFragment_to_searchLocationFragment) }
            currentHourRv.adapter = hourListAdapter
            forecastRv.isNestedScrollingEnabled = false
            forecastRv.adapter = forecastDaysListAdapter
        }


        lifecycleScope.launch(Dispatchers.Main) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    todaySearchSharedViewModel.locationStateFlow.collect { pair ->
                        pair?.let { weatherTodayViewModel.onLoadWithGeo(pair) }
                    }
                }

                launch {
                    todaySearchSharedViewModel.searchFieldStateFlow.collect { cityName ->
                        cityName?.let { weatherTodayViewModel.onLoadWithCityName(it) }
                    }
                }

                launch {
                    weatherTodayViewModel.state.collect {
                        when (it) {
                            is WeatherTodayUiState.LoadCompleteDataState -> onDataCompleteLoading(it.weatherTodayUi)
                            is WeatherTodayUiState.LoadingState -> onLoading()
                            is WeatherTodayUiState.ErrorState -> onError(it.errorTxtId)
                            is WeatherTodayUiState.EmptyLocationState -> onEmptySharedPref()
                        }
                    }
                }

                launch {
                    weatherTodayViewModel.routineWorkerState.collect { workerConfigState ->
                        when (workerConfigState) {
                            is WorkerConfigState.DataHolder -> setupWeatherNotifyWorker(workerConfigState.workerConfig)
                            is WorkerConfigState.Default -> Unit
                        }
                    }
                }
            }
        }
    }

    // Checking if device api is 33+. If it is, so we should call for user permission. Then creating worker
    private fun setupWeatherNotifyWorker(workerConfig: WorkerConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.i("WorkerManagingFlow", "setupWeatherRetrieverWorker: phone api is 33+.. so requesting for user permission")
            PermissionManager(this, object : PermissionManager.PermissionCallback {
                override fun onPermissionGranted() {
                    Log.i("WorkerManagingFlow", "setupWeatherRetrieverWorker: permission granted... creating worker")
                    createWeatherNotifyWorker(workerConfig)
                }

                override fun onPermissionDenied() {
                    Toast.makeText(requireContext(), getString(R.string.user_permission_denied, "Notification"), Toast.LENGTH_LONG).show()
                }
            }).checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Log.i("WorkerManagingFlow", "setupWeatherRetrieverWorker: Phone api is below 33... Creating worker")
            createWeatherNotifyWorker(workerConfig)
        }
    }

    // Creates worker with configs view model told fragment
    private fun createWeatherNotifyWorker(workerConfig: WorkerConfig) {
        Log.i("WorkerManagingFlow", "createRetrieverWorker: Im trying to create worker. Please wait...")
        PeriodicWorkRequestBuilder<WeatherNotifyWorker>(workerConfig.intervalHours, TimeUnit.HOURS)
            .setInitialDelay(workerConfig.initialDelay, TimeUnit.MILLISECONDS)
            .build()
            .also { worker ->
                Log.i("WorkerManagingFlow", "createRetrieverWorker: worker CREATED properly")
                WorkManager.getInstance(requireContext()).enqueue(worker)
                Log.i("WorkerManagingFlow", "createRetrieverWorker: worker ENQUEUED properly")
                weatherTodayViewModel.onWorkerSet()
            }
    }

    private fun onLoading() {
        binding.run {
            dataCl.visibility = GONE
            loadingFl.visibility = VISIBLE
            errorLl.visibility = GONE
        }
    }

    private fun onError(@StringRes txtId: Int) {
        requireBinding {
            dataCl.visibility = GONE
            loadingFl.visibility = GONE
            errorLl.visibility = VISIBLE
            errorTv.text = getString(txtId)
            retryBtn.setOnClickListener { weatherTodayViewModel.onRetryClick() }
        }
    }

    // No initial city name found. Navigating user to search fragment
    private fun onEmptySharedPref() {
        findNavController().navigate(R.id.action_weatherTodayFragment_to_searchLocationFragment)
    }

    private fun onDataCompleteLoading(weatherTodayUi: WeatherTodayUi) {
        binding.run {
            dataCl.visibility = VISIBLE
            loadingFl.visibility = GONE
            errorLl.visibility = GONE
        }
        updateUI(weatherTodayUi)
    }

    private fun updateUI(weatherTodayUi: WeatherTodayUi) {
        binding.run {
            weatherTodayUi.currentWeatherUi.let {
                currentLocationTv.text = it.locationName
                currentTempTv.text = getString(R.string.degree_sign, it.tempC)
                currentConditionTv.text = it.conditionText
                ImageLoader.load(requireContext(), it.icon, currentConditionIv)
                minMaxFeelTempTv.text = getString(R.string.min_max_feels_like, it.minTempC, it.maxTempC, it.feelsLikeC)
                windSpeedTv.text = getString(R.string.speed_km, it.windSpeedKph)
                humidityTv.text = getString(R.string.humidity, it.humidity)
                pressureTv.text = getString(R.string.pressure, it.pressureMb)
                currentTimeTv.text = it.date.stylishDate(requireContext())
            }
        }

        hourListAdapter.submitList(weatherTodayUi.hourUiList)
        forecastDaysListAdapter.submitList(weatherTodayUi.forecastDayUiList)
    }
}