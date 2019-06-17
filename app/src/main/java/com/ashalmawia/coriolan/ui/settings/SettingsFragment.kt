package com.ashalmawia.coriolan.ui.settings

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceDataStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ashalmawia.coriolan.BuildConfig
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.takisoft.fix.support.v7.preference.EditTextPreference
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers
import org.koin.android.ext.android.inject

private const val PREFERENCE_CARD_TYPES = "card_types"
private const val PREFERENCE_DAILY_LIMITS_NEW = "daily_limit_new_cards"
private const val PREFERENCE_DAILY_LIMITS_REVIEW = "daily_limit_review_cards"
private const val PREFERENCE_VERSION = "app_version"

class SettingsFragment : PreferenceFragmentCompatDividers() {

    private val dataStore: PreferenceDataStore by inject()

    private val cardTypePreferenceHelper: CardTypePreferenceHelper = CardTypePreferenceHelperImpl()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context == null) {
            return
        }
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = dataStore
        addPreferencesFromResource(R.xml.settings)

        setUpCardTypes()
        setUpDailyLimits()
        setUpVersionInfo()
    }

    private fun setUpVersionInfo() {
        findPreference(PREFERENCE_VERSION).summary = BuildConfig.VERSION_NAME
    }

    private fun setUpDailyLimits() {
        val limitNew = findPreference(PREFERENCE_DAILY_LIMITS_NEW) as EditTextPreference
        val limitReview = findPreference(PREFERENCE_DAILY_LIMITS_REVIEW) as EditTextPreference

        limitNew.setOnPreferenceChangeListener { _, value ->
            val stringValue = value as String
            verifyDailyLimit(stringValue)
        }

        limitReview.setOnPreferenceChangeListener { _, value ->
            val stringValue = value as String
            verifyDailyLimit(stringValue)
        }
    }

    private fun setUpCardTypes() {
        val cardTypes = findPreference(PREFERENCE_CARD_TYPES) as ListPreference
        cardTypePreferenceHelper.initialize(cardTypes)
    }

    private fun verifyDailyLimit(value: String): Boolean {
        if (value.isEmpty()) {
            return true
        }

        return try {
            val int = value.toInt()
            if (int < 0) {
                showMessage(getString(R.string.settings__error_number_negative, value))
                false
            } else {
                true
            }
        } catch (e: NumberFormatException) {
            showMessage(getString(R.string.settings__error_number_incorrect, value))
            false
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {
            return super.onCreateView(inflater, container, savedInstanceState)
        } finally {
            setDividerPreferences(DIVIDER_PADDING_CHILD
                    or DIVIDER_CATEGORY_AFTER_LAST
                    or DIVIDER_CATEGORY_BETWEEN)
        }
    }
}

class CoriolanPreferencesDataStore(
        private val prefs: Preferences,
        private val cardTypePreferenceHelper: CardTypePreferenceHelper
) : PreferenceDataStore() {

    override fun putString(key: String?, value: String?) {
        when (key ?: return) {
            PREFERENCE_DAILY_LIMITS_NEW ->
                if (!TextUtils.isEmpty(value)) prefs.setNewCardsDailyLimitDefault(value!!.toInt()) else prefs.clearNewCardsDailyLimit()

            PREFERENCE_DAILY_LIMITS_REVIEW ->
                if (!TextUtils.isEmpty(value)) prefs.setReviewCardsDailyLimitDefault(value!!.toInt()) else prefs.clearReviewCardsDailyLimit()

            PREFERENCE_CARD_TYPES ->
                cardTypePreferenceHelper.saveValue(prefs, value)
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        return when (key ?: return null) {
            PREFERENCE_DAILY_LIMITS_NEW ->
               prefs.getNewCardsDailyLimitDefault()?.toString()

            PREFERENCE_DAILY_LIMITS_REVIEW ->
                prefs.getReviewCardsDailyLimitDefault()?.toString()

            PREFERENCE_CARD_TYPES ->
                cardTypePreferenceHelper.getCurrentValue(prefs)

            else ->
                super.getString(key, defValue)
        }
    }
}