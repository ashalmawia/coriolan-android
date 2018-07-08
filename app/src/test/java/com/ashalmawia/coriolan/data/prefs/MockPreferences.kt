package com.ashalmawia.coriolan.data.prefs

import org.joda.time.DateTime

class MockPreferences : Preferences {

    private var newCardsDailyLimitDefault: Int? = null
    override fun getNewCardsDailyLimitDefault(): Int? = newCardsDailyLimitDefault
    override fun setNewCardsDailyLimitDefault(limit: Int) {
        newCardsDailyLimitDefault = limit
    }
    override fun clearNewCardsDailyLimit() {
        newCardsDailyLimitDefault = null
        newCardsDailyLimits.clear()
    }

    private val newCardsDailyLimits = mutableMapOf<String, Int>()
    override fun getNewCardsDailyLimit(date: DateTime): Int?
            = newCardsDailyLimits[date.toString()] ?: getNewCardsDailyLimitDefault()
    override fun setNewCardsDailyLimit(limit: Int, date: DateTime) {
        newCardsDailyLimits[date.toString()] = limit
    }

    private var reviewCardsDailyLimitDefault: Int? = null
    override fun getReviewCardsDailyLimitDefault(): Int? = reviewCardsDailyLimitDefault
    override fun setReviewCardsDailyLimitDefault(limit: Int) {
        reviewCardsDailyLimitDefault = limit
    }
    override fun clearReviewCardsDailyLimit() {
        reviewCardsDailyLimitDefault = null
        reviewCardsDailyLimits.clear()
    }

    private val reviewCardsDailyLimits = mutableMapOf<String, Int>()
    override fun getReviewCardsDailyLimit(date: DateTime): Int?
            = reviewCardsDailyLimits[date.toString()] ?: getReviewCardsDailyLimitDefault()
    override fun setReviewCardsDailyLimit(limit: Int, date: DateTime) {
        reviewCardsDailyLimits[date.toString()] = limit
    }

    private var firstStart: Boolean? = null
    override fun isFirstStart(): Boolean {
        return firstStart ?: true
    }
    override fun recordFirstStart() {
        firstStart = false
    }

    private var cardType: CardTypePreference? = null
    override fun getCardTypePreference(): CardTypePreference? {
        return cardType
    }
    override fun setCardTypePreference(preference: CardTypePreference) {
        cardType = preference
    }
}