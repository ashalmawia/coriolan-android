package com.ashalmawia.coriolan.ui.edit

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.ashalmawia.coriolan.model.Deck

class EditListBuilder {

    private val list = mutableListOf<EditListItem>()

    fun addCategory(@StringRes title: Int): EditListBuilder {
        list.add(EditListItem.CategoryItem(title))
        return this
    }

    fun addDecks(decks: List<Deck>, listener: EditDeckCallback): EditListBuilder {
        list.addAll(decks.map { EditListItem.DeckItem(it, listener) })
        return this
    }

    fun addOption(@StringRes title: Int, onClick: (Context) -> Unit, @DrawableRes icon: Int? = null): EditListBuilder {
        list.add(EditListItem.OptionItem(title, icon, onClick))
        return this
    }

    fun build(): List<EditListItem> = list
}