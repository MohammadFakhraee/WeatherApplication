package com.example.weatherapplication.feature.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapplication.core.base.BaseListAdapter
import com.example.weatherapplication.databinding.ItemCitySearchBinding
import com.example.weatherapplication.feature.search.uimodel.CitySearchUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Adapter class which extends [BaseListAdapter] to perform internal notify changes on item list changes.
 * It is used to wrap [CitySearchUi] inside its view holder called [CitySearchViewHolder]
 * @see BaseListAdapter to find out why we used it as a base adapter class
 */
class CitySearchListAdapter @Inject constructor() :
    BaseListAdapter<CitySearchUi, CitySearchListAdapter.CitySearchViewHolder>(CitySearchItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySearchViewHolder =
        CitySearchViewHolder(ItemCitySearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    inner class CitySearchViewHolder(
        private val itemCitySearchBinding: ItemCitySearchBinding
    ) : BaseListAdapter.BaseViewHolder<CitySearchUi>(itemCitySearchBinding.root) {

        override fun onBind(item: CitySearchUi) {
            itemCitySearchBinding.run {
                searchedTv.text = item.searchTxt
                completeTv.text = item.completeTxt

                root.setOnClickListener { onItemClickListener?.invoke(item) }
            }
        }
    }

    class CitySearchItemDiffUtil : DiffUtil.ItemCallback<CitySearchUi>() {
        override fun areItemsTheSame(oldItem: CitySearchUi, newItem: CitySearchUi): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CitySearchUi, newItem: CitySearchUi): Boolean = oldItem == newItem
    }
}
