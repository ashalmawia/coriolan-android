package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.dependencies.*
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.util.OpenForTesting
import com.ashalmawia.errors.Errors
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@OpenForTesting
class CoriolanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        crashlytics()
        errors()

        initializeDependencies()

        todayManager()

        runFirstStartRoutine()
    }

    @OpenForTesting
    protected fun initializeDependencies() {
        startKoin {
            androidContext(this@CoriolanApplication)
            modules(koinModules)
        }
    }

    @OpenForTesting
    protected fun runFirstStartRoutine() {
        val firstStart: FirstStart = get()
        firstStart.runFirstStartRoutine()
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

    fun onDomainChanged(domain: Domain) {
        getKoin().recreateDomainScope(domain)
    }
}