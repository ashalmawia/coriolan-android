package com.ashalmawia.coriolan.model

data class Card(
        val id: Long,
        val deckId: Long,
        val domain: Domain,
        val original: Expression,
        val translations: List<Expression>
) {
    val type: CardType
        get() {
            return if (original.language == domain.langOriginal()) CardType.FORWARD else CardType.REVERSE
        }
}

enum class CardType {
    UNKNOWN, FORWARD, REVERSE
}