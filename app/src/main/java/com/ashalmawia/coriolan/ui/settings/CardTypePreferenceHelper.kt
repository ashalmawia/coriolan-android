package com.ashalmawia.coriolan.ui.settings

import android.content.Context
import android.support.v7.preference.ListPreference
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences

object CardTypePreferenceHelper {

    private val VALUES = CardTypePreference.values().map { it.value }.toTypedArray()

    private val ENTRIES = arrayOf(
            R.string.settings__card_types_mixed,
            R.string.settings__card_types_forward_only,
            R.string.settings__card_types_reverse_only
    )

    fun initialize(context: Context, preference: ListPreference) {
        preference.entryValues = VALUES
        preference.entries = ENTRIES.map { context.getString(it) }.toTypedArray()
    }

    fun saveValue(preferences: Preferences, value: String?) {
        if (value == null) {
            throw IllegalArgumentException("value must not be null")
        }

        val preference = CardTypePreference.from(value)
        if (preference == null) {
            return
        } else {
            preferences.setCardTypePreference(preference)
        }
    }

    fun getCurrentValue(preferences: Preferences): String? {
        val value = preferences.getCardTypePreference()
        if (value == null) {
            return null
        } else {
            return value.value
        }
    }
}