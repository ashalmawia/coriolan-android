package com.ashalmawia.coriolan.learning

interface ExercisesRegistry {

    fun allExercises(): List<Exercise<*, *>>

    fun defaultExercise(): Exercise<*, *>
}