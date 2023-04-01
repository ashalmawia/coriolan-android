package com.ashalmawia.coriolan.model

data class Term(
        val id: Long,
        val value: String,
        val language: Language,
        val extras: Extras
)

data class Extras(
        val transcription: String?
) {
    companion object {
        fun empty() = Extras(null)
    }
}