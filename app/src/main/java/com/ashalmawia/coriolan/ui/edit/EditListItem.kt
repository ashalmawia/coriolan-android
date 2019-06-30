package com.ashalmawia.coriolan.ui.edit

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.model.Deck

sealed class EditListItem {
    abstract val type: EditListItemType

    data class CategoryItem(@StringRes val title: Int) : EditListItem() {
        override val type: EditListItemType
            get() = EditListItemType.CATEGORY
    }

    data class DeckItem(val deck: Deck, val listener: EditDeckCallback) : EditListItem() {
        override val type: EditListItemType
            get() = EditListItemType.DECK
    }

    data class OptionItem(@StringRes val title: Int, @DrawableRes val icon: Int?, val onClick: (Context) -> Unit) : EditListItem() {
        override val type: EditListItemType
            get() = EditListItemType.OPTION
    }
}

interface EditDeckCallback {

    fun addCards(deck: Deck)

    fun editDeck(deck: Deck)

    fun deleteDeck(context: Context, deck: Deck)
}