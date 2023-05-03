package com.ashalmawia.coriolan.ui.commons.decks_list

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class BaseDeckListBuilder<Data> {

    private val list = mutableListOf<BaseDeckListItem>()

    fun addCategory(@StringRes title: Int): BaseDeckListBuilder<Data> {
        list.add(BaseDeckListItem.CategoryItem(title))
        return this
    }

    fun addDecks(decks: List<Data>): BaseDeckListBuilder<Data> {
        list.addAll(decks.map { BaseDeckListItem.DeckItem(it) })
        return this
    }

    fun addOption(@StringRes title: Int, onClick: (Context) -> Unit, @DrawableRes icon: Int? = null): BaseDeckListBuilder<Data> {
        list.add(BaseDeckListItem.OptionItem(title, icon, onClick))
        return this
    }

    fun build(): List<BaseDeckListItem> = list
}