package com.ashalmawia.coriolan.model

data class TermExtra(
        val id: Long,
        val type: ExtraType,
        val value: String
)

enum class ExtraType(val value: Int) {

    UNKNOWN(0),
    TRANSCRIPTION(1);

    companion object {

        fun from(value: Int) = values().find { it.value == value } ?: UNKNOWN
    }
}