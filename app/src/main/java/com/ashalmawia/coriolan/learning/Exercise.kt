package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.model.Card

interface Exercise {

    /**
     * Unique string ID which must never be changed.
     * Data storing relies on it.
     */
    val stableId: String

    /**
     * Each exercise must have it's name
     */
    @StringRes
    fun name(): Int

    fun show(context: Context, card: Card)

    /**
     * Some exercises might want to only work with specific cards (e.g. already seen)
     */
    fun prefilter(cards: List<Card>): List<Card> {
        return cards
    }
}