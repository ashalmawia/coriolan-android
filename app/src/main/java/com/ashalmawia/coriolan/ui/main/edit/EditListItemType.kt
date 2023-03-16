package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ashalmawia.coriolan.R

enum class EditListItemType {
    CATEGORY {
        override fun createViewHolder(context: Context, parent: ViewGroup): EditListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.edit_list_category_item, parent, false)
            return EditListViewHolder.Category(view)
        }

        override fun bindViewHolder(holder: EditListViewHolder, item: EditListItem) {
            (holder as EditListViewHolder.Category).bind(item as EditListItem.CategoryItem)
        }
    },
    DECK {
        override fun createViewHolder(context: Context, parent: ViewGroup): EditListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.edit_list_deck_item, parent, false)
            return EditListViewHolder.Deck(view)
        }

        override fun bindViewHolder(holder: EditListViewHolder, item: EditListItem) {
            (holder as EditListViewHolder.Deck).bind(item as EditListItem.DeckItem)
        }
    },
    OPTION {
        override fun createViewHolder(context: Context, parent: ViewGroup): EditListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.edit_list_option_item, parent, false)
            return EditListViewHolder.Option(view)
        }

        override fun bindViewHolder(holder: EditListViewHolder, item: EditListItem) {
            (holder as EditListViewHolder.Option).bind(item as EditListItem.OptionItem)
        }
    };

    abstract fun createViewHolder(context: Context, parent: ViewGroup): EditListViewHolder
    abstract fun bindViewHolder(holder: EditListViewHolder, item: EditListItem)
}