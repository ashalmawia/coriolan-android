package com.ashalmawia.coriolan.learning

import android.content.Context

interface ExercisesRegistry {

    companion object {
        private var instance: ExercisesRegistry? = null

        fun get(context: Context): ExercisesRegistry {
            return instance ?: ExercisesRegistryImpl(context.applicationContext).also { instance = it }
        }
    }

    fun allExercises(): List<Exercise<*, *>>

    fun defaultExercise(): Exercise<*, *>
}