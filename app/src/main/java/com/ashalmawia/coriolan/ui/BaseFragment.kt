package com.ashalmawia.coriolan.ui

import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment() {

    private fun requireActivity() = activity!! as BaseActivity

    protected fun requireActivityScope() = requireActivity().requireScope()

    protected val navigator: Navigator
        get() = requireActivityScope().get()
}