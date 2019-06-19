package com.ashalmawia.coriolan.learning

import android.content.Context

class ExercisesRegistry(context: Context) {

    companion object {
        private var instance: ExercisesRegistry? = null

        fun get(context: Context): ExercisesRegistry {
            return instance ?: ExercisesRegistry(context.applicationContext).also { instance = it }
        }
    }

    private val default = LearningExercise(context)

    private val exercises = listOf(
            default
            // all others go here
    )

    fun allExercises(): List<Exercise<*, *>> {
        return exercises
    }

    fun defaultExercise(): Exercise<*, *> {
        return default
    }
}