package com.ashalmawia.coriolan.data.prefs

import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.model.LanguageId
import org.joda.time.DateTime

interface Preferences {

    fun isFirstStart(): Boolean
    fun recordFirstStart()

    fun isOnboardingCompleted(): Boolean
    fun recordOnboardingCompleted()

    fun isMainFeatureDiscoverySeen(): Boolean
    fun recordMainFeatureDiscoverySeen()

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

    fun getLastTranslationsLanguageId(): LanguageId?
    fun setLastTranslationsLanguageId(language: Language)
    fun clearLastTranslationsLanguageId()

    var mixForwardAndReverse: Boolean
}