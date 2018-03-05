package com.ashalmawia.coriolan.model

data class Domain(
        val id: Long,
        val name: String,
        private val langOriginal: Language,
        private val langTranslations: Language
) {

    fun langOriginal(type: CardType = CardType.FORWARD): Language {
        return when (type) {
            CardType.FORWARD -> langOriginal
            CardType.REVERSE -> langTranslations

            CardType.UNKNOWN -> throw IllegalArgumentException("unexpected type[$type]")
        }
    }

    fun langTranslations(type: CardType = CardType.FORWARD): Language {
        return when (type) {
            CardType.FORWARD -> langTranslations
            CardType.REVERSE -> langOriginal

            CardType.UNKNOWN -> throw IllegalArgumentException("unexpected type[$type]")
        }
    }
}