package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.model.Card

class MockExercise(private val name: String = "mock") : Exercise {
    override val stableId: String
        get() = name

    override fun name(): Int = 0

    override fun show(context: Context, card: Card) {
        // do nothing, I'm a mock!
    }
}