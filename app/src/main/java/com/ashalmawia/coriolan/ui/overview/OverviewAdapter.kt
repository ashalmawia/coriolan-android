package com.ashalmawia.coriolan.ui.overview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ashalmawia.coriolan.databinding.OverviewListItemBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.ui.commons.list.FlexListAdapter
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.commons.list.FlexListViewHolder

class OverviewAdapter(private val callback: Callback) : FlexListAdapter<CardViewHolder, Card>() {

    override fun createEntityViewHolder(context: Context, parent: ViewGroup): CardViewHolder {
        val layoutInflater = LayoutInflater.from(context)
        val binding = OverviewListItemBinding.inflate(layoutInflater, parent, false)
        return CardViewHolder(binding, callback)
    }

    interface Callback {
        fun onCardClicked(card: Card)
    }
}

class CardViewHolder(
        private val views: OverviewListItemBinding,
        private val callback: OverviewAdapter.Callback
) : FlexListViewHolder.EntityItem<Card>(views.root) {

    override fun bind(item: FlexListItem.EntityItem<Card>) {
        val card = item.entity
        views.title.text = card.original.value
        views.root.setOnClickListener { callback.onCardClicked(card) }
    }
}