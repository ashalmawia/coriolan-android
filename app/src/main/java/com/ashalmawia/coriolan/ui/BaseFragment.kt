package com.ashalmawia.coriolan.ui

import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected fun showLoading() {
        (requireActivity() as BaseActivity).showLoading()
    }

    protected fun hideLoading() {
        (requireActivity() as BaseActivity).hideLoading()
    }
}