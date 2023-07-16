package com.ashalmawia.coriolan.ui.util

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.ashalmawia.coriolan.ui.BaseActivity

@MainThread
inline fun <reified VM : ViewModel> BaseActivity.viewModelBuilder(
        noinline viewModelInitializer: () -> VM
): Lazy<VM> {
    return ViewModelLazy(
            viewModelClass = VM::class,
            storeProducer = { viewModelStore },
            factoryProducer = {
                return@ViewModelLazy object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")// Casting T as ViewModel
                        return viewModelInitializer.invoke() as T
                    }
                }
            }
    )
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModelBuilder(
        noinline viewModelInitializer: () -> VM
): Lazy<VM> {
    return ViewModelLazy(
            viewModelClass = VM::class,
            storeProducer = { requireActivity().viewModelStore },
            factoryProducer = {
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")// Casting T as ViewModel
                        return viewModelInitializer.invoke() as T
                    }
                }
            }
    )
}