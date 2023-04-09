package com.ashalmawia.coriolan.data.prefs

import android.content.Context
import com.ashalmawia.coriolan.model.Language
import org.joda.time.DateTime

class SharedPreferencesImpl(context: Context) : Preferences {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val dailyLimitsNew = context.getSharedPreferences("daily_limits_new", Context.MODE_PRIVATE)
    private val dailyLimitsReview = context.getSharedPreferences("daily_limits_review", Context.MODE_PRIVATE)

    override fun isFirstStart(): Boolean {
        return prefs.getBoolean(IS_FIRST_START, true)
    }

    override fun recordFirstStart() {
        prefs.edit().putBoolean(IS_FIRST_START, false).apply()
    }

    override fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(IS_ONBOARDING_COMPLETED, false)
    }

    override fun recordOnboardingCompleted() {
        prefs.edit().putBoolean(IS_ONBOARDING_COMPLETED, true).apply()
    }

    override fun getNewCardsDailyLimitDefault(): Int? {
        return dailyLimitsNew.getIntOrNull(DEFAULT)
    }

    override fun getNewCardsDailyLimit(date: DateTime): Int? {
        val overriden = dailyLimitsNew.getIntOrNull(date.toString())
        return overriden ?: getNewCardsDailyLimitDefault()
    }

    override fun setNewCardsDailyLimitDefault(limit: Int) {
        dailyLimitsNew.edit().putInt(DEFAULT, limit).apply()
    }

    override fun setNewCardsDailyLimit(limit: Int, date: DateTime) {
        dailyLimitsNew.edit().putInt(date.toString(), limit).apply()
    }

    override fun clearNewCardsDailyLimit() {
        dailyLimitsNew.edit().clear().apply()
    }

    override fun getReviewCardsDailyLimitDefault(): Int? {
        return dailyLimitsReview.getIntOrNull(DEFAULT)
    }

    override fun getReviewCardsDailyLimit(date: DateTime): Int? {
        val overriden = dailyLimitsReview.getIntOrNull(date.toString())
        return overriden ?: getReviewCardsDailyLimitDefault()
    }

    override fun setReviewCardsDailyLimitDefault(limit: Int) {
        dailyLimitsReview.edit().putInt(DEFAULT, limit).apply()
    }

    override fun setReviewCardsDailyLimit(limit: Int, date: DateTime) {
        dailyLimitsReview.edit().putInt(date.toString(), limit).apply()
    }

    override fun clearReviewCardsDailyLimit() {
        dailyLimitsReview.edit().clear().apply()
    }

    override fun getLastTranslationsLanguageId(): Long? {
        return prefs.getLongOrNull(LAST_TRANSLATIONS_LANGUAGE)
    }

    override fun setLastTranslationsLanguageId(language: Language) {
        prefs.edit().putLong(LAST_TRANSLATIONS_LANGUAGE, language.id).apply()
    }

    override fun clearLastTranslationsLanguageId() {
        prefs.edit().remove(LAST_TRANSLATIONS_LANGUAGE).apply()
    }
}

private const val IS_FIRST_START = "is_first_start"
private const val IS_ONBOARDING_COMPLETED = "is_onboarding_completed"
private const val DEFAULT = "default"
private const val LAST_TRANSLATIONS_LANGUAGE = "last_translations_language"