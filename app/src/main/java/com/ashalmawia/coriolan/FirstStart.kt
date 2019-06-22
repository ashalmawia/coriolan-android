package com.ashalmawia.coriolan

import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences

private const val NEW_CARDS_DAILY_LIMIT_DEFAULT = 15
private const val REVIEW_CARDS_DAILY_LIMIT_DEFAULT = 30

private val CARD_TYPE_PREFERENCE_DEFAULT = CardTypePreference.MIXED

interface FirstStart {

    fun runFirstStartRoutine()
}

class FirstStartImpl(private val preferences: Preferences) : FirstStart {

    override fun runFirstStartRoutine() {
        if (!preferences.isFirstStart()) {
            return
        }

        putDefaultPreferences(preferences)
    }

    private fun putDefaultPreferences(preferences: Preferences) {
        preferences.setNewCardsDailyLimitDefault(NEW_CARDS_DAILY_LIMIT_DEFAULT)
        preferences.setReviewCardsDailyLimitDefault(REVIEW_CARDS_DAILY_LIMIT_DEFAULT)
        preferences.setCardTypePreference(CARD_TYPE_PREFERENCE_DEFAULT)

        preferences.recordFirstStart()
    }
}