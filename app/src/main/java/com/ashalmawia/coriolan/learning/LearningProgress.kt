package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.learning.exercise.sr.emptyState

data class LearningProgress(
    val states: Map<ExerciseId, ExerciseState>
) {
    val spacedRepetition: ExerciseState
        get() = states[ExerciseId.FLASHCARDS] ?: emptyState()
}