package com.ashalmawia.coriolan.learning.exercise.flashcards

interface SpacedRepetitionScheduler {

    fun processAnswer(answer: FlashcardsAnswer, state: ExerciseState): ExerciseState
}