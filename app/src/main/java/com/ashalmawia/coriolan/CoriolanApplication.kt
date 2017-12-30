package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.DecksRegistry

open class CoriolanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        preinitialize()
    }

    protected open fun preinitialize() {
        DecksRegistry.preinitialize(this)
    }
}