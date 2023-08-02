package com.ashalmawia.coriolan.learning.exercise.flashcards

import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.ui.learning.CardAnswer

interface SpacedRepetitionScheduler {

    fun processAnswer(answer: CardAnswer, state: SchedulingState): SchedulingState
}