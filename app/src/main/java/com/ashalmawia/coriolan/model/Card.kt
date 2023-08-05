package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import org.joda.time.DateTime
import java.lang.IllegalArgumentException

data class Card(
        val id: Long,
        val deckId: DeckId,
        val domain: Domain,
        val type: CardType,
        val original: Term,
        val translations: List<Term>,
        val dateAdded: DateTime
)

enum class CardType(val value: String) {
    FORWARD("forward"), REVERSE("reverse");

    fun toCardTypeFilter(): CardTypeFilter {
        return when (this) {
            FORWARD -> CardTypeFilter.FORWARD
            REVERSE -> CardTypeFilter.REVERSE
        }
    }

    companion object {
        fun fromValue(value: String) = values().find { it.value == value }
                ?: throw IllegalArgumentException("unexpected card type: $value")
    }
}