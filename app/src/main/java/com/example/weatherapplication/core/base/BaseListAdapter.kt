package com.example.weatherapplication.core.base

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.core.base.BaseListAdapter.BaseViewHolder

/**
 * Base adapter used for recyclerViews which has only one item. It cannot be used for more than one items!
 * This class is extending [ListAdapter] which will handle data add, remove and changes with the help of [DiffUtil.ItemCallback]
 * and notifies required method to notify that the data is changed.
 * @param T type of input items to show in [RecyclerView].
 * @param Q type of the view holder item. It should be extended from [BaseViewHolder].
 * @param itemCallback DiffUtil item which calculates item changes
 * @see DiffUtil.ItemCallback for more details
 */
abstract class BaseListAdapter<T, Q : BaseViewHolder<T>>(itemCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, Q>(itemCallback) {

    /**
     * A listener used to call when an item is clicked (if the owner fragment or activity
     * needs to have a callback and the subclass calls it)
     */
    var onItemClickListener: ((T) -> Unit)? = null

    /**
     * Calls [BaseViewHolder.onBind] method and passes the item of type [T]
     * which its list index would be [position].
     */
    override fun onBindViewHolder(holder: Q, position: Int) = holder.onBind(getItem(position))

    /**
     * Base Class for adapter's view holder. It defines a type [P] which is as the same as type [T]
     * and owns a function to be called when [onBindViewHolder] is triggered.
     */
    abstract class BaseViewHolder<P>(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun onBind(item: P)
    }
}
