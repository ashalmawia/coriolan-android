package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.scheduler.TodayManager
import com.ashalmawia.errors.Errors
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

open class CoriolanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        crashlytics()
        errors()

        todayManager()

        domainsRegistry()
    }

    private fun crashlytics() {
        val core = CrashlyticsCore.Builder().build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())
    }

    private fun errors() {
        val errors = Errors.Builder().useCrashlytics().debug(BuildConfig.DEBUG).build()
        Errors.with(errors)
    }

    private fun todayManager() {
        TodayManager.initialize(this)
    }

    protected open fun domainsRegistry() {
        DomainsRegistry.preinitialize(Repository.get(this))
    }
}