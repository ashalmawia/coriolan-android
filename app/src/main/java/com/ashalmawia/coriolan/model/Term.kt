package com.ashalmawia.coriolan.model

import java.io.Serializable

data class Term(
        val id: TermId,
        val value: String,
        val language: Language,
        val transcription: String?
)

data class TermId(val value: Long) : Serializable {
    fun asString() = value.toString()
}