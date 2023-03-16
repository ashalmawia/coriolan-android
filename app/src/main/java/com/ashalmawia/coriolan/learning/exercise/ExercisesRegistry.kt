package com.ashalmawia.coriolan.learning.exercise

interface ExercisesRegistry {

    fun allExercises(): List<Exercise<*, *>>

    fun defaultExercise(): Exercise<*, *>
}