package com.ashalmawia.coriolan.ui.main.edit

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListAdapter
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListItem
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListViewHolder

class EditDeckListAdapter(
        private val callback: EditDeckCallback
) : BaseDeckListAdapter<EditDeckViewHolder, Deck>(R.layout.edit_list_deck_item) {

    override fun createDeckViewHolder(view: View): EditDeckViewHolder {
        return EditDeckViewHolder(view, callback)
    }
}

class EditDeckViewHolder(
        itemView: View, private val listener: EditDeckCallback
) : BaseDeckListViewHolder.DeckItem<Deck>(itemView) {

    val title: TextView = itemView.findViewById(R.id.title)
    val add: ImageView = itemView.findViewById(R.id.add)
    val edit: ImageView = itemView.findViewById(R.id.edit)
    val delete: ImageView = itemView.findViewById(R.id.delete)

    override fun bind(item: BaseDeckListItem.DeckItem<Deck>) {
        title.text = item.deck.name
        add.setOnClickListener { listener.addCards(it.context, item.deck) }
        edit.setOnClickListener { listener.editDeck(it.context, item.deck) }
        delete.setOnClickListener { listener.deleteDeck(it.context, item.deck) }
    }
}