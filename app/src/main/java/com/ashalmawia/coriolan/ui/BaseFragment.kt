package com.ashalmawia.coriolan.ui

import android.support.v4.app.Fragment
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.dependencies.domainScope
import org.kodein.di.KodeinAware

abstract class BaseFragment : Fragment(), KodeinAware {

    override val kodein by org.kodein.di.android.kodein(context())

    protected fun decksRegistry() = DecksRegistry.get(
            context(),
            domainScope().get()
    )

    private fun context() =
            activity?.applicationContext ?: throw IllegalStateException("expected to have been attached to activity")
}