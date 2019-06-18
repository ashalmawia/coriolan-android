package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.dependencies.Dependecies
import com.ashalmawia.coriolan.dependencies.KodeinDependencies
import com.ashalmawia.coriolan.learning.scheduler.TodayManager
import com.ashalmawia.coriolan.util.OpenForTesting
import com.ashalmawia.errors.Errors
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric

@OpenForTesting
class CoriolanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        crashlytics()
        errors()

        todayManager()

//        firstStartJobs(preferences)
    }

    val dependencies: Dependecies = KodeinDependencies()

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

    protected fun firstStartJobs(preferences: Preferences) {
        FirstStart.preinitializeIfFirstStart(preferences)
    }

//    companion object {
//        fun onDomainChanged(koin: Koin, domain: Domain) {
//            koin.setDomain(domain)
//        }
//    }
}