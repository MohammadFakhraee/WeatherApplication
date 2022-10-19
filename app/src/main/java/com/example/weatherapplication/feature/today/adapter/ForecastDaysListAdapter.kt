package com.example.weatherapplication.feature.today.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapplication.R
import com.example.weatherapplication.core.base.BaseListAdapter
import com.example.weatherapplication.databinding.ItemWeatherDayBinding
import com.example.weatherapplication.feature.today.adapter.ForecastDaysListAdapter.ForecastDaysViewHolder
import com.example.weatherapplication.feature.today.uimodel.ForecastDayUi
import com.example.weatherapplication.util.ImageLoader
import com.example.weatherapplication.util.day
import com.example.weatherapplication.util.dayOfWeek
import com.example.weatherapplication.util.stylishDate
import javax.inject.Inject

/**
 * Adapter class which extends [BaseListAdapter] to perform internal notify changes on item list changes.
 * It is used to wrap [ForecastDayUi] inside its view holder called [ForecastDaysViewHolder]
 * @see BaseListAdapter to find out why we used it as a base adapter class
 */

class ForecastDaysListAdapter @Inject constructor() :
    BaseListAdapter<ForecastDayUi, ForecastDaysListAdapter.ForecastDaysViewHolder>(ForecastDayUiDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastDaysViewHolder =
        ForecastDaysViewHolder(ItemWeatherDayBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class ForecastDaysViewHolder(private val itemWeatherDayBinding: ItemWeatherDayBinding) :
        BaseListAdapter.BaseViewHolder<ForecastDayUi>(itemWeatherDayBinding.root) {

        override fun onBind(item: ForecastDayUi) {
            itemWeatherDayBinding.run {
                Log.i(TAG, "onBind: item loaded with position = $absoluteAdapterPosition")
                dayOfWeekTv.text = item.date.dayOfWeek()
                dayOfMonthTv.text = item.date.stylishDate(root.context)
                tempTv.text = root.context.getString(R.string.min_max, item.minTempC, item.maxTempC)
                ImageLoader.load(root.context, item.icon, dayIconIv)
            }
        }
    }

    class ForecastDayUiDiffUtil : DiffUtil.ItemCallback<ForecastDayUi>() {
        override fun areItemsTheSame(oldItem: ForecastDayUi, newItem: ForecastDayUi): Boolean = oldItem.date.day() == newItem.date.day()

        override fun areContentsTheSame(oldItem: ForecastDayUi, newItem: ForecastDayUi): Boolean = oldItem == newItem
    }

    companion object {
        private const val TAG = "ForecastDaysListAdapter"
    }
}