package com.ashalmawia.coriolan.data.prefs

import android.content.Context
import com.ashalmawia.coriolan.model.Language
import org.joda.time.DateTime

interface Preferences {

    companion object {
        private lateinit var instance: Preferences

        fun get(context: Context): Preferences {
            if (!Companion::instance.isInitialized) {
                instance = SharedPreferencesImpl(context)
            }
            return instance
        }
    }

    fun isFirstStart(): Boolean
    fun recordFirstStart()

    fun getNewCardsDailyLimitDefault(): Int?
    fun getNewCardsDailyLimit(date: DateTime): Int?
    fun setNewCardsDailyLimitDefault(limit: Int)
    fun setNewCardsDailyLimit(limit: Int, date: DateTime)
    fun clearNewCardsDailyLimit()

    fun getReviewCardsDailyLimitDefault(): Int?
    fun getReviewCardsDailyLimit(date: DateTime): Int?
    fun setReviewCardsDailyLimitDefault(limit: Int)
    fun setReviewCardsDailyLimit(limit: Int, date: DateTime)
    fun clearReviewCardsDailyLimit()

    fun getCardTypePreference(): CardTypePreference?
    fun setCardTypePreference(preference: CardTypePreference)

    fun getLastTranslationsLanguageId(): Long?
    fun setLastTranslationsLanguageId(language: Language)
}