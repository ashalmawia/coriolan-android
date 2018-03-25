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

    override fun getCardTypePreference(): CardTypePreference? {
        val value = prefs.getString(CARDY_TYPE_PREFERENCE, null)
        return if (value == null) {
            null
        } else {
            CardTypePreference.from(value)
        }
    }

    override fun setCardTypePreference(preference: CardTypePreference) {
        prefs.edit().putString(CARDY_TYPE_PREFERENCE, preference.value).apply()
    }
}

private const val IS_FIRST_START = "is_first_start"
private const val DAILY_LIMIT_NEW_CARDS = "daily_limit_new_cards"
private const val DAILY_LIMIT_REVIEW_CARDS = "daily_limit_review_cards"
private const val CARDY_TYPE_PREFERENCE = "card_type_preference"