package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.learning.exercise.sr.emptyState
import com.ashalmawia.coriolan.model.Card

data class Task(val card: Card, val learningProgress: LearningProgress, val exercise: Exercise) {

    val exerciseState: ExerciseState
        get() = learningProgress.states[exercise.id] ?: emptyState()

    fun learningProgressWithUpdatedExerciseState(newState: ExerciseState): LearningProgress {
        return LearningProgress(learningProgress.states.toMutableMap().apply { this[exercise.id] = newState })
    }
}