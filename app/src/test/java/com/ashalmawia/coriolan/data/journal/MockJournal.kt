package com.ashalmawia.coriolan.data.journal

import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime

class MockJournal : Journal {

    private val data = mutableMapOf<CardAction, Int>()

    fun setTodayLearned(new: Int, review: Int) {
        data[CardAction.NEW_CARD_FIRST_SEEN] = new
        data[CardAction.CARD_REVIEWED] = review
    }

    override fun cardsStudiedOnDate(date: DateTime): Counts {
        return Counts(
                data[CardAction.NEW_CARD_FIRST_SEEN].orZero(),
                data[CardAction.CARD_REVIEWED].orZero(),
                data[CardAction.CARD_RELEARNED].orZero(), -1)
    }

    override fun cardsStudiedOnDate(date: DateTime, exercise: ExerciseId): Counts {
        return Counts(
                data[CardAction.NEW_CARD_FIRST_SEEN].orZero(),
                data[CardAction.CARD_REVIEWED].orZero(),
                data[CardAction.CARD_RELEARNED].orZero(), -1)
    }

    override fun incrementCardActions(date: DateTime, exercise: ExerciseId, cardAction: CardAction) {
        data[cardAction] = data[cardAction].orZero() + 1
    }

    override fun decrementCardActions(date: DateTime, exercise: ExerciseId, cardAction: CardAction) {
        data[cardAction] = data[cardAction].orZero() - 1
    }
}