package com.example.weatherapplication.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.weatherapplication.util.binding.ViewBindingHandler
import com.example.weatherapplication.util.binding.ViewBindingHandlerImpl


/**
 * Base class used for fragments. This class owns the implementation of ViewBinding in the scope of its lifecycle
 * @param T type of viewBinding used in subclass fragments.
 * Subclasses which extend this class won't need to worry about the memory leak of their ViewBinding class.
 * @see com.example.weatherapplication.util.binding.ViewBindingHandler
 * @see com.example.weatherapplication.util.binding.ViewBindingHandlerImpl
 */
abstract class BaseFragment<T : ViewBinding> : Fragment(), ViewBindingHandler<T> by ViewBindingHandlerImpl() {

    /**
     * This method initializes and requests for viewBinding scope handling in the following steps:
     * 1. Calls [createViewBinding] method for the creation of the ViewBinding.
     * 2. Calls [initBinding] method and passes the response of step 1, fragments lifecycle instance and a lambda function to call
     * when the initializing is complete.
     * 3. The [initBinding] returns the root view of the ViewBinding passed.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initBinding(createViewBinding(inflater, container), viewLifecycleOwner.lifecycle) { onInitBindingCallback() }
    }

    /**
     * Creates the instance of viewBinding of type [T]
     * @return [T] the instance of viewBinding of the subclasses
     */
    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    /**
     * function which will be triggered in subclasses as soon as the viewBinding is initialized and stored in the [ViewBindingHandlerImpl].
     * This function is used to replace [onCreateView] overriding.
     */
    abstract fun onInitBindingCallback()
}
