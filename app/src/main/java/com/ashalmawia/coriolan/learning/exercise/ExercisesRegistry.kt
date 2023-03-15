package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.exercise.Exercise

interface ExercisesRegistry {

    fun allExercises(): List<Exercise<*, *>>

    fun defaultExercise(): Exercise<*, *>
}