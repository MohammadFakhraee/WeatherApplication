package com.example.weatherapplication.util.binding

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.viewbinding.ViewBinding

interface ViewBindingHandler<T : ViewBinding> {

    /**
     * Back property of view binding variable.
     */
    val binding: T

    fun <R> requireBinding(callback: T.() -> R): R?

    /**
     * Initials and handles lifecycle of Fragment(or activity)'s view binding
     * @param binding the viewBinding item.
     * @param lifecycle an instance of Fragment(or Activity)'s lifecycle. In order to release binding from memory when is not needed
     * @param onBind a callback of which the binding is being setup completely.
     * It can be null which means the caller does not want to have a callback
     * @return View rootView of the viewBinding
     */
    fun initBinding(binding: T, lifecycle: Lifecycle, onBind: (T.() -> Unit)? = null): View
}