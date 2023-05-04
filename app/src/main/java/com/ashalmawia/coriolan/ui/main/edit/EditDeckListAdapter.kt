package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ashalmawia.coriolan.databinding.EditListDeckItemBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.commons.list.FlexListAdapter
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.commons.list.FlexListViewHolder

class EditDeckListAdapter(
        private val callback: EditDeckCallback
) : FlexListAdapter<EditDeckViewHolder, Deck>() {

    override fun createEntityViewHolder(context: Context, parent: ViewGroup): EditDeckViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = EditListDeckItemBinding.inflate(layoutInflater, parent, false)
        return EditDeckViewHolder(binding, callback)
    }
}

class EditDeckViewHolder(
        private val views: EditListDeckItemBinding, private val listener: EditDeckCallback
) : FlexListViewHolder.EntityItem<Deck>(views.root) {

    override fun bind(item: FlexListItem.EntityItem<Deck>) {
        views.title.text = item.entity.name

        views.root.setOnClickListener { listener.onDeckClicked(item.entity) }
        views.add.setOnClickListener { listener.addCards(item.entity) }
        views.edit.setOnClickListener { listener.editDeck(item.entity) }
        views.delete.setOnClickListener { listener.deleteDeck(item.entity) }
    }
}