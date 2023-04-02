package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.learning.exercise.sr.emptyState

data class LearningProgress(
    val states: Map<ExerciseId, ExerciseState>
) {
    val globalStatus: Status
        get() {
            if (states.isEmpty()) return Status.NEW
            if (states.any { it.value.status == Status.RELEARN}) return Status.RELEARN
            if (states.all { it.value.status == Status.NEW }) return Status.NEW
            if (states.all { it.value.status == Status.LEARNT }) return Status.LEARNT
            return Status.IN_PROGRESS
        }
    val spacedRepetition: ExerciseState
        get() = stateFor(ExerciseId.FLASHCARDS)

    fun stateFor(exerciseId: ExerciseId) = states[exerciseId] ?: emptyState()
    fun statusFor(exerciseId: ExerciseId) = stateFor(exerciseId).status
}