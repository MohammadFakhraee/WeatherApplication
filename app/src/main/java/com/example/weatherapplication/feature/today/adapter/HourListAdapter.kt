package com.example.weatherapplication.feature.today.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapplication.R
import com.example.weatherapplication.core.base.BaseListAdapter
import com.example.weatherapplication.databinding.ItemWeatherHourBinding
import com.example.weatherapplication.feature.today.adapter.HourListAdapter.ItemHourViewHolder
import com.example.weatherapplication.feature.today.uimodel.HourUi
import com.example.weatherapplication.util.ImageLoader
import javax.inject.Inject

/**
 * Adapter class which extends [BaseListAdapter] to perform internal notify changes on item list changes.
 * It is used to wrap [HourUi] inside its view holder called [ItemHourViewHolder]
 * @see BaseListAdapter to find out why we used it as a base adapter class
 */
class HourListAdapter @Inject constructor() : BaseListAdapter<HourUi, HourListAdapter.ItemHourViewHolder>(HourUiDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHourViewHolder =
        ItemHourViewHolder(ItemWeatherHourBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class ItemHourViewHolder(private val itemWeatherHourBinding: ItemWeatherHourBinding) :
        BaseListAdapter.BaseViewHolder<HourUi>(itemWeatherHourBinding.root) {
        override fun onBind(item: HourUi) {
            itemWeatherHourBinding.run {
                tempTv.text = root.context.getString(R.string.degree_sign, item.tempC)
                timeTv.text = root.context.getString(if (item.isBeforeNoon) R.string.am else R.string.pm, item.time)
                ImageLoader.load(root.context, item.icon, conditionIv)
            }
        }
    }

    class HourUiDiffUtilCallback : DiffUtil.ItemCallback<HourUi>() {
        override fun areItemsTheSame(oldItem: HourUi, newItem: HourUi): Boolean = oldItem.time == newItem.time

        override fun areContentsTheSame(oldItem: HourUi, newItem: HourUi): Boolean = oldItem == newItem
    }
}