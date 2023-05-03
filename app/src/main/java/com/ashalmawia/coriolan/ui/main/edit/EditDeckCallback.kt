package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import com.ashalmawia.coriolan.model.Deck

interface EditDeckCallback {

    fun addCards(context: Context, deck: Deck)

    fun editDeck(context: Context, deck: Deck)

    fun deleteDeck(context: Context, deck: Deck)
}