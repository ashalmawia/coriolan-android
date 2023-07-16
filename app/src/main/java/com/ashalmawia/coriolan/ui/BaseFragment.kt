package com.ashalmawia.coriolan.ui

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    fun showLoading() {
        (requireActivity() as BaseActivity).showLoading()
    }

    fun hideLoading() {
        (requireActivity() as BaseActivity).hideLoading()
    }
}