package com.ashalmawia.coriolan.ui.commons.decks_list

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.util.setStartDrawableTint

sealed class BaseDeckListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class CategoryItem(itemView: View): BaseDeckListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: BaseDeckListItem.CategoryItem) {
            title.setText(item.title)
        }
    }

    abstract class DeckItem<Data>(itemView: View): BaseDeckListViewHolder(itemView) {
        abstract fun bind(item: BaseDeckListItem.DeckItem<Data>)
    }

    class OptionItem(itemView: View): BaseDeckListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: BaseDeckListItem.OptionItem) {
            title.setText(item.title)
            if (item.icon != null) {
                title.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, 0, 0, 0)
                title.setStartDrawableTint(R.color.colorPrimary)
            } else {
                title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
            itemView.setOnClickListener { item.onClick(it.context) }
        }
    }
}