package com.ashalmawia.coriolan.learning.exercise

import android.content.Context
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.model.Card

class MockExercise : Exercise {
    override val stableId: String
        get() = "mock"

    override fun name(): Int = 0

    override fun show(context: Context, card: Card) {
        // do nothing, I'm a mock!
    }
}