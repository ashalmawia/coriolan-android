package com.ashalmawia.coriolan.ui.settings

import android.content.Context
import android.os.Bundle
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDataStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.takisoft.fix.support.v7.preference.EditTextPreference
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompatDividers

private const val PREFERENCE_CARD_TYPES = "card_types"
private const val PREFERENCE_DAILY_LIMITS_NEW = "daily_limit_new_cards"
private const val PREFERENCE_DAILY_LIMITS_REVIEW = "daily_limit_review_cards"

class SettingsFragment : PreferenceFragmentCompatDividers() {

    private lateinit var prefs: Preferences
    private lateinit var dataStore: PreferenceDataStore

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context == null) {
            return
        }

        prefs = Preferences.get(context)
        dataStore = CoriolanPreferencesDataStore(prefs)
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = dataStore
        addPreferencesFromResource(R.xml.settings)

        setUpCardTypes()
        setUpDailyLimits()
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
        val context = context ?: return

        val cardTypes = findPreference(PREFERENCE_CARD_TYPES) as ListPreference
        CardTypePreferenceHelper.initialize(context, cardTypes)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        super.onDisplayPreferenceDialog(preference)
    }

    private fun verifyDailyLimit(value: String): Boolean {
        if (value.isEmpty()) {
            return true
        }

        return try {
            val int = value.toInt()
            if (int < 0) {
                showMessage("Number incorrect")
                false
            } else {
                true
            }
        } catch (e: NumberFormatException) {
            showMessage("Number incorrect")
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
            setDividerPreferences(PreferenceFragmentCompatDividers.DIVIDER_PADDING_CHILD
                    or PreferenceFragmentCompatDividers.DIVIDER_CATEGORY_AFTER_LAST
                    or PreferenceFragmentCompatDividers.DIVIDER_CATEGORY_BETWEEN)
        }
    }
}

class CoriolanPreferencesDataStore(val prefs: Preferences) : PreferenceDataStore() {

    override fun putString(key: String?, value: String?) {
        when (key ?: return) {
            PREFERENCE_DAILY_LIMITS_NEW ->
                if (!TextUtils.isEmpty(value)) prefs.setNewCardsDailyLimit(value!!.toInt()) else prefs.clearNewCardsDailyLimit()

            PREFERENCE_DAILY_LIMITS_REVIEW ->
                if (!TextUtils.isEmpty(value)) prefs.setReviewCardsDailyLimit(value!!.toInt()) else prefs.clearReviewCardsDailyLimit()

            PREFERENCE_CARD_TYPES ->
                CardTypePreferenceHelper.saveValue(prefs, value)
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        return when (key ?: return null) {
            PREFERENCE_DAILY_LIMITS_NEW ->
               prefs.getNewCardsDailyLimit()?.toString()

            PREFERENCE_DAILY_LIMITS_REVIEW ->
                prefs.getReviewCardsDailyLimit()?.toString()

            PREFERENCE_CARD_TYPES ->
                CardTypePreferenceHelper.getCurrentValue(prefs)

            else ->
                super.getString(key, defValue)
        }
    }
}