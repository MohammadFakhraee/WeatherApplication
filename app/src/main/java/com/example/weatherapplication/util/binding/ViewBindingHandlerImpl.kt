package com.example.weatherapplication.util.binding

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

class ViewBindingHandlerImpl<T : ViewBinding> : ViewBindingHandler<T>, LifecycleEventObserver {

    private var lifecycle: Lifecycle? = null
    private var _binding: T? = null

    /**
     * It would null-safe if the children classes call [initBinding] in their creation lifecycle.
     * Otherwise, the variable would be null which will result in [NullPointerException].
     */
    override val binding: T get() = _binding!!

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lifecycle?.removeObserver(this)
            lifecycle = null
            _binding = null
        }
    }

    override fun <R> requireBinding(callback: T.() -> R): R? {
        return _binding?.callback()
    }

    override fun initBinding(binding: T, lifecycle: Lifecycle, onBind: (T.() -> Unit)?): View {
        _binding = binding
        this.lifecycle = lifecycle
        this.lifecycle?.addObserver(this)
        onBind?.invoke(binding)
        return binding.root
    }
}