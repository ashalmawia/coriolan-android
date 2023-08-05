package com.ashalmawia.coriolan.data.logbook

import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.DeckId

data class LogbookPayload(
        val cardActions: LogbookCardActionsPayload
) {
    companion object {
        fun create() = LogbookPayload(LogbookCardActionsPayload.create())
    }
}

data class LogbookCardActionsPayload(
        val byExercise: MutableMap<ExerciseId, LogbookCardActionsPayloadEntry>,
        val byDeck: MutableMap<Long, LogbookCardActionsPayloadEntry>,
        val total: LogbookCardActionsPayloadEntry
) {
    fun increment(exerciseId: ExerciseId, deckId: DeckId, cardAction: CardAction) {
        process(exerciseId, deckId, cardAction, Count::increment)
    }
    fun decrement(exerciseId: ExerciseId, deckId: DeckId, cardAction: CardAction) {
        process(exerciseId, deckId, cardAction, Count::decrement)
    }

    fun byExercise(exerciseId: ExerciseId) = byExercise.getOrPut(exerciseId) { LogbookCardActionsPayloadEntry.create() }
    fun byDeck(deckId: DeckId) = byDeck.getOrPut(deckId.value) { LogbookCardActionsPayloadEntry.create() }

    private fun process(exerciseId: ExerciseId, deckId: DeckId, cardAction: CardAction, update: Count.() -> Unit) {
        byExercise(exerciseId)
                .map.getOrPut(cardAction) { Count.zero() }
                .update()
        byDeck(deckId)
                .map.getOrPut(cardAction) { Count.zero() }
                .update()
        total.map.getOrPut(cardAction) { Count.zero() }
                .update()
    }

    companion object {
        fun create() = LogbookCardActionsPayload(
                mutableMapOf(), mutableMapOf(), LogbookCardActionsPayloadEntry.create())
    }
}

data class LogbookCardActionsPayloadEntry(
        val map: MutableMap<CardAction, Count>
) {
    fun unwrap() = map.mapValues { it.value.count }

    companion object {
        fun create() = LogbookCardActionsPayloadEntry(mutableMapOf())
    }
}

data class Count(var count: Int) {
    fun increment() {
        count++
    }

    fun decrement() {
        count--
    }

    companion object {
        fun zero() = Count(0)
    }
}