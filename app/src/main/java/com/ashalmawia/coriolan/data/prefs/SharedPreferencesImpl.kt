package com.ashalmawia.coriolan.data.prefs

import android.content.Context

class SharedPreferencesImpl(context: Context) : Preferences {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun isFirstStart(): Boolean {
        return prefs.getBoolean(IS_FIRST_START, true)
    }

    override fun recordFirstStart() {
        prefs.edit().putBoolean(IS_FIRST_START, false).apply()
    }

    override fun getDefaultDeckId(): Long? {
        return prefs.getLongOrNull(DEFAULT_DECK_ID)
    }

    override fun setDefaultDeckId(id: Long) {
        prefs.edit().putLong(DEFAULT_DECK_ID, id).apply()
    }

    override fun getNewCardsDailyLimit(): Int? {
        return prefs.getIntOrNull(DAILY_LIMIT_NEW_CARDS)
    }

    override fun setNewCardsDailyLimit(limit: Int) {
        prefs.edit().putInt(DAILY_LIMIT_NEW_CARDS, limit).apply()
    }

    override fun clearNewCardsDailyLimit() {
        prefs.edit().remove(DAILY_LIMIT_NEW_CARDS).apply()
    }

    override fun getReviewCardsDailyLimit(): Int? {
        return prefs.getIntOrNull(DAILY_LIMIT_REVIEW_CARDS)
    }

    override fun setReviewCardsDailyLimit(limit: Int) {
        prefs.edit().putInt(DAILY_LIMIT_REVIEW_CARDS, limit).apply()
    }

    override fun clearReviewCardsDailyLimit() {
        prefs.edit().remove(DAILY_LIMIT_REVIEW_CARDS).apply()
    }
}

private const val IS_FIRST_START = "is_first_start"
private const val DEFAULT_DECK_ID = "default_deck_id"
private const val DAILY_LIMIT_NEW_CARDS = "daily_limit_new_cards"
private const val DAILY_LIMIT_REVIEW_CARDS = "daily_limit_review_cards"