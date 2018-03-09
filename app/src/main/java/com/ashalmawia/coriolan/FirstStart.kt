package com.ashalmawia.coriolan

import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences

private const val NEW_CARDS_DAILY_LIMIT_DEFAULT = 15
private const val REVIEW_CARDS_DAILY_LIMIT_DEFAULT = 30

private val CARD_TYPE_PREFERENCE_DEFAULT = CardTypePreference.FORWARD_FIRST

object FirstStart {

    fun preinitializeIfFirstStart(preferences: Preferences) {
        if (!preferences.isFirstStart()) {
            return
        }

        preferences.setNewCardsDailyLimit(NEW_CARDS_DAILY_LIMIT_DEFAULT)
        preferences.setReviewCardsDailyLimit(REVIEW_CARDS_DAILY_LIMIT_DEFAULT)
        preferences.setCardTypePreference(CARD_TYPE_PREFERENCE_DEFAULT)

        preferences.recordFirstStart()
    }
}