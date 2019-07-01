package com.ashalmawia.coriolan.model

data class CardData(
        val original: String,
        val transcription: String?,
        val translations: List<String>,
        val deckId: Long
)