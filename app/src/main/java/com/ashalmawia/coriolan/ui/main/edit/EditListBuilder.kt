package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListItem

class EditListBuilder {

    private val list = mutableListOf<BaseDeckListItem>()

    fun addCategory(@StringRes title: Int): EditListBuilder {
        list.add(BaseDeckListItem.CategoryItem(title))
        return this
    }

    fun addDecks(decks: List<Deck>): EditListBuilder {
        list.addAll(decks.map { BaseDeckListItem.DeckItem(it) })
        return this
    }

    fun addOption(@StringRes title: Int, onClick: (Context) -> Unit, @DrawableRes icon: Int? = null): EditListBuilder {
        list.add(BaseDeckListItem.OptionItem(title, icon, onClick))
        return this
    }

    fun build(): List<BaseDeckListItem> = list
}