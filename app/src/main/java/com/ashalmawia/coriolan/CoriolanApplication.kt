package com.ashalmawia.coriolan

import android.app.Application
import com.ashalmawia.coriolan.data.DecksRegistry

class CoriolanApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DecksRegistry.preinitialize(this)
    }
}