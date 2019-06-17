package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.dependencies.domainModule
import com.ashalmawia.coriolan.dependencies.mainModule
import com.ashalmawia.coriolan.util.OpenForTesting
import com.ashalmawia.errors.Errors
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@OpenForTesting
class CoriolanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        crashlytics()
        errors()

        todayManager()

        startKoin {
            androidContext(this@CoriolanApplication)
            modules(listOf(mainModule, domainModule))
        }

        firstStartJobs(get())
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
}