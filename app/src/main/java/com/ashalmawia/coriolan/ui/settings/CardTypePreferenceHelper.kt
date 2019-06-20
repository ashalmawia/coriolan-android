package com.ashalmawia.coriolan.ui.settings

import android.support.v7.preference.ListPreference
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.CardTypePreference
import com.ashalmawia.coriolan.data.prefs.Preferences

interface CardTypePreferenceHelper {

    fun initialize(preference: ListPreference)

    fun saveValue(preferences: Preferences, value: String?)

    fun getCurrentValue(preferences: Preferences): String?
}

class CardTypePreferenceHelperImpl : CardTypePreferenceHelper {

    private val VALUES = CardTypePreference.values().map { it.value }.toTypedArray()

    private val ENTRIES = arrayOf(
            R.string.settings__card_types_mixed,
            R.string.settings__card_types_forward_only,
            R.string.settings__card_types_reverse_only
    )

    override fun initialize(preference: ListPreference) {
        preference.entryValues = VALUES
        preference.entries = ENTRIES.map { preference.context.getString(it) }.toTypedArray()
    }

    override fun saveValue(preferences: Preferences, value: String?) {
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

    override fun getCurrentValue(preferences: Preferences): String? {
        val value = preferences.getCardTypePreference()
        return if (value == null) {
            null
        } else {
            value.value
        }
    }
}