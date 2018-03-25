package com.ashalmawia.coriolan.data.prefs

class MockPreferences : Preferences {

    private var newCardsDailyLimit: Int? = null
    override fun getNewCardsDailyLimit(): Int? = newCardsDailyLimit
    override fun setNewCardsDailyLimit(limit: Int) {
        newCardsDailyLimit = limit
    }
    override fun clearNewCardsDailyLimit() {
        newCardsDailyLimit = null
    }

    private var reviewCardsDailyLimit: Int? = null
    override fun getReviewCardsDailyLimit(): Int? = reviewCardsDailyLimit
    override fun setReviewCardsDailyLimit(limit: Int) {
        reviewCardsDailyLimit = limit
    }
    override fun clearReviewCardsDailyLimit() {
        reviewCardsDailyLimit = null
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