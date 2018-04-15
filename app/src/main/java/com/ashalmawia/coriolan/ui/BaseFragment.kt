package com.ashalmawia.coriolan.ui

import android.support.v4.app.Fragment
import com.ashalmawia.coriolan.data.DecksRegistry

abstract class BaseFragment : Fragment() {

    protected fun decksRegistry() = DecksRegistry.get(
            activity?.applicationContext ?: throw IllegalStateException("expected to have been attached to activity")
    )
}