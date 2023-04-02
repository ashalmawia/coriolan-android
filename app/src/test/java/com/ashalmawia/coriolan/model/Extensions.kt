package com.ashalmawia.coriolan.model

import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState


val LearningProgress.mock: ExerciseState
    get() = stateFor(ExerciseId.TEST)