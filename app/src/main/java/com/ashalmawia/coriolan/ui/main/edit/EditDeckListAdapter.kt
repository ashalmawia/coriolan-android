package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.commons.list.FlexListAdapter
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.commons.list.FlexListViewHolder

class EditDeckListAdapter(
        private val callback: EditDeckCallback
) : FlexListAdapter<EditDeckViewHolder, Deck>() {

    override fun createEntityViewHolder(context: Context, parent: ViewGroup): EditDeckViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.edit_list_deck_item, parent, false)
        return EditDeckViewHolder(view, callback)
    }
}

class EditDeckViewHolder(
        itemView: View, private val listener: EditDeckCallback
) : FlexListViewHolder.EntityItem<Deck>(itemView) {

    val title: TextView = itemView.findViewById(R.id.title)
    val add: ImageView = itemView.findViewById(R.id.add)
    val edit: ImageView = itemView.findViewById(R.id.edit)
    val delete: ImageView = itemView.findViewById(R.id.delete)

    override fun bind(item: FlexListItem.EntityItem<Deck>) {
        title.text = item.entity.name
        add.setOnClickListener { listener.addCards(it.context, item.entity) }
        edit.setOnClickListener { listener.editDeck(it.context, item.entity) }
        delete.setOnClickListener { listener.deleteDeck(it.context, item.entity) }
    }
}