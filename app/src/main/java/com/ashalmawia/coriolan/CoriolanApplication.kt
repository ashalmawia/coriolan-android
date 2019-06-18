package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.dependencies.init
import com.ashalmawia.coriolan.dependencies.mainModule
import com.ashalmawia.coriolan.learning.scheduler.TodayManager
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.util.OpenForTesting
import com.ashalmawia.errors.Errors
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

@OpenForTesting
class CoriolanApplication : Application(), KodeinAware {

    private val preferences: Preferences by instance()

    override fun onCreate() {
        super.onCreate()

        crashlytics()
        errors()

        todayManager()

        firstStartJobs(preferences)
    }

    override val kodein: Kodein by Kodein.lazy {
        import(mainModule)
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

    protected fun firstStartJobs(preferences: Preferences) {
        FirstStart.preinitializeIfFirstStart(preferences)
    }

    companion object {
        fun onDomainChanged(kodein: Kodein, domain: Domain) {

        }
    }
}