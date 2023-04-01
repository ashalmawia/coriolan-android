package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.model.Card

interface ExerciseLogbook {

    fun recordCardAction(card: Card, oldLearningProgress: LearningProgress, newLearningProgress: LearningProgress)

    fun unrecordCardAction(card: Card, learningProgress: LearningProgress, learningProgressThatWasUndone: LearningProgress)
}

enum class CardAction(val value: String) {
    NEW_CARD_FIRST_SEEN("opened"),
    CARD_REVIEWED("reviewed"),
    CARD_RELEARNED("relearned")
}