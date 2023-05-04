package com.ashalmawia.coriolan.ui.main.edit

import com.ashalmawia.coriolan.model.Deck

interface EditDeckCallback {

    fun onDeckClicked(deck: Deck)

    fun addCards(deck: Deck)

    fun editDeck(deck: Deck)

    fun deleteDeck(deck: Deck)
}