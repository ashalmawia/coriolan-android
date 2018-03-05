package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.LanguagesRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
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

        languages()
        domainsRegistry()
        deckRegistry()
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

    protected open fun deckRegistry() {
        DecksRegistry.initialize(this, Preferences.get(this), DomainsRegistry.domain(), Repository.get(this))
    }

    protected open fun languages() {
        LanguagesRegistry.createStubOriginalAndTranslationsIfNeeded(this)
        LanguagesRegistry.preinitialize(this)
    }
}