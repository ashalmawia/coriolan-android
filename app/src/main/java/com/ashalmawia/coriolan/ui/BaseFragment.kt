package com.ashalmawia.coriolan.ui

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    fun showLoading() {
        val activity = activity ?: return
        (activity as BaseActivity).showLoading()
    }

    fun hideLoading() {
        val activity = activity ?: return
        (activity as BaseActivity).hideLoading()
    }
}