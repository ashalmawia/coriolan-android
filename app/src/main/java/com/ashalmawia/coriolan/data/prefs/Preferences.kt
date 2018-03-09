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

    fun isFirstStart(): Boolean
    fun recordFirstStart()

    fun getDefaultDeckId(): Long?
    fun setDefaultDeckId(id: Long)

    fun getNewCardsDailyLimit(): Int?
    fun setNewCardsDailyLimit(limit: Int)
    fun clearNewCardsDailyLimit()

    fun getReviewCardsDailyLimit(): Int?
    fun setReviewCardsDailyLimit(limit: Int)
    fun clearReviewCardsDailyLimit()

    fun getCardTypePreference(): CardTypePreference?
    fun setCardTypePreference(preference: CardTypePreference)
}