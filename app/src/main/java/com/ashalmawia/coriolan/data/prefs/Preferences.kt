package com.ashalmawia.coriolan.data.prefs

import android.content.Context

interface Preferences {

    companion object {
        private lateinit var instance: Preferences

        fun get(context: Context): Preferences {
            if (!Preferences.Companion::instance.isInitialized) {
                instance = SharedPreferencesImpl(context)
            }
            return instance
        }
    }

    fun getDefaultDeckId(): Long?
    fun setDefaultDeckId(id: Long)
}