package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.model.Card

data class Task(val card: Card, val learningProgress: LearningProgress, val exercise: Exercise) {

    fun learningProgressWithUpdatedExerciseState(newState: SchedulingState): LearningProgress {
        return learningProgress.copy(state = newState)
    }

    fun isPending() = learningProgress.state.due <= TodayManager.today()
}