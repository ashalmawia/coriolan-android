package com.ashalmawia.coriolan.learning

import android.content.Context
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.model.Card

interface Exercise {
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