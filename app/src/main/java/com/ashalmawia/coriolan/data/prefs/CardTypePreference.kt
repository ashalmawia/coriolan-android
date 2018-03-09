package com.ashalmawia.coriolan.data.prefs

enum class CardTypePreference(val value: String) {
    FORWARD_FIRST("forward_first"),
    REVERSE_FIRST("reverse_first"),
    MIXED("mixed"),
    FORWARD_ONLY("forward_only"),
    REVERSE_ONLY("reverse_only");

    companion object {
        fun from(value: String): CardTypePreference? = values().find { it.value == value }
    }
}