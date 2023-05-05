package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.EditListDeckItemBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.commons.list.FlexListAdapter
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.commons.list.FlexListViewHolder

class EditDeckListAdapter(
        private val callback: EditDeckCallback
) : FlexListAdapter<EditDeckViewHolder, EditDeckListItem>() {

    override fun createEntityViewHolder(context: Context, parent: ViewGroup): EditDeckViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = EditListDeckItemBinding.inflate(layoutInflater, parent, false)
        return EditDeckViewHolder(binding, callback)
    }
}

class EditDeckViewHolder(
        private val views: EditListDeckItemBinding, private val listener: EditDeckCallback
) : FlexListViewHolder.EntityItem<EditDeckListItem>(views.root) {

    private val context: Context
        get() = views.root.context

    override fun bind(item: FlexListItem.EntityItem<EditDeckListItem>) {
        val deck = item.entity.deck

        views.title.text = deck.name
        val cardsCount = item.entity.cardsCount
        views.subtitle.text = context.resources.getQuantityString(R.plurals.cards_count, cardsCount, cardsCount)

        views.root.setOnClickListener { listener.onDeckClicked(deck) }
        views.add.setOnClickListener { listener.addCards(deck) }
        views.edit.setOnClickListener { listener.editDeck(deck) }
        views.delete.setOnClickListener { listener.deleteDeck(deck) }
    }
}