package com.ashalmawia.coriolan.model

import java.io.Serializable

data class Domain(
        val id: DomainId,
        private val customName: String?,
        private val langOriginal: Language,
        private val langTranslations: Language
) {
    val name: String = if (!customName.isNullOrBlank()) customName else langOriginal.value

    fun langOriginal(type: CardType = CardType.FORWARD): Language {
        return when (type) {
            CardType.FORWARD -> langOriginal
            CardType.REVERSE -> langTranslations
        }
    }

    fun langTranslations(type: CardType = CardType.FORWARD): Language {
        return when (type) {
            CardType.FORWARD -> langTranslations
            CardType.REVERSE -> langOriginal
        }
    }
}

data class DomainId(val value: Long) : Serializable {

    fun asString() = value.toString()
}