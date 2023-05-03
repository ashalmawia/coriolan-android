package com.ashalmawia.coriolan.ui.commons.decks_list

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class BaseDeckListItem {
    abstract val type: BaseDeckListItemType

    data class CategoryItem(@StringRes val title: Int) : BaseDeckListItem() {
        override val type: BaseDeckListItemType
            get() = BaseDeckListItemType.CATEGORY
    }

    data class DeckItem<Data>(val deck: Data) : BaseDeckListItem() {
        override val type: BaseDeckListItemType
            get() = BaseDeckListItemType.DECK
    }

    data class OptionItem(@StringRes val title: Int, @DrawableRes val icon: Int?, val onClick: (Context) -> Unit) : BaseDeckListItem() {
        override val type: BaseDeckListItemType
            get() = BaseDeckListItemType.OPTION
    }
}