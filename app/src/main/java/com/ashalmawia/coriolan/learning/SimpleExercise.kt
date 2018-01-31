package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.CardActivity
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.model.Card

/**
 * Simple learning exercise which shows all the cards in the assignment: given front, guess back.
 * After seeing the back side, user asserts themselves.
 *
 * If the card is answered correctly, it removes it from the queue.
 * Otherwise, adds it to the end of the queue.
 */
class SimpleExercise() : Exercise {

    override val stableId: String
        get() = "simple"

    @StringRes
    override fun name(): Int {
        return R.string.exercise_simple
    }

    override fun show(context: Context, card: Card) {
        val intent = CardActivity.intent(context)
        context.startActivity(intent)
    }
}