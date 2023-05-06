package com.ashalmawia.coriolan.learning.exercise.flashcards

import com.ashalmawia.coriolan.learning.SchedulingState

interface SpacedRepetitionScheduler {

    fun processAnswer(answer: FlashcardsAnswer, state: SchedulingState): SchedulingState
}