package com.ashalmawia.coriolan.learning.exercise.flashcards

import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.ui.learning.CardViewAnswer

interface SpacedRepetitionScheduler {

    fun processAnswer(answer: CardViewAnswer, state: SchedulingState): SchedulingState
}