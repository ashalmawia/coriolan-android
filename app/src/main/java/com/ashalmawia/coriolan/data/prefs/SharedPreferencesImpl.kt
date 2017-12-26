package com.ashalmawia.coriolan.data.prefs

import android.content.Context

class SharedPreferencesImpl(context: Context) : Preferences {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun getDefaultDeckId(): Long? {
        return if (prefs.contains(SHARED_DEFAULT_DECK_ID))
            prefs.getLong(SHARED_DEFAULT_DECK_ID, 0L)
        else
            null
    }

    override fun setDefaultDeckId(id: Long) {
        prefs.edit().putLong(SHARED_DEFAULT_DECK_ID, id).apply()
    }
}

private const val SHARED_DEFAULT_DECK_ID = "default_deck_id"