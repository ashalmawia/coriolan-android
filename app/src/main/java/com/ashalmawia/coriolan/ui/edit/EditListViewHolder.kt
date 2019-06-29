package com.ashalmawia.coriolan.ui.edit

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.util.setStartDrawableTint

sealed class EditListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class Category(itemView: View): EditListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: EditListItem.CategoryItem) {
            title.setText(item.title)
        }
    }

    class Deck(itemView: View): EditListViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val add: ImageView = itemView.findViewById(R.id.add)
        val edit: ImageView = itemView.findViewById(R.id.edit)
        val delete: ImageView = itemView.findViewById(R.id.delete)
        
        fun bind(item: EditListItem.DeckItem) {
            title.text = item.deck.name
            add.setOnClickListener { item.listener.addCards(it.context, item.deck) }
            edit.setOnClickListener { item.listener.editDeck(it.context, item.deck) }
            delete.setOnClickListener { item.listener.deleteDeck(it.context, item.deck) }
        }
    }

    class Option(itemView: View): EditListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: EditListItem.OptionItem) {
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