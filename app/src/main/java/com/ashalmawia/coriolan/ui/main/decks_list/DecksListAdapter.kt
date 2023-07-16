package com.ashalmawia.coriolan.ui.main.decks_list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DeckListItemBinding
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.ui.commons.list.FlexListAdapter
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.commons.list.FlexListViewHolder
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.view.layoutInflator
import com.ashalmawia.coriolan.ui.view.visible

class DecksListAdapter(private val listener: DeckListAdapterListener)
    : FlexListAdapter<DeckListDeckViewHolder, DeckListItem>(), DeckListDeckViewHolder.Callback {

    override fun createEntityViewHolder(context: Context, parent: ViewGroup): DeckListDeckViewHolder {
        val views = DeckListItemBinding.inflate(parent.layoutInflator, parent, false)
        return DeckListDeckViewHolder(views, this)
    }

    override fun showPopupMenu(item: DeckListItem, anchor: View) {
        val menu = PopupMenu(anchor.context, anchor)
        menu.inflate(R.menu.decks_study_options_popup)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.decks_study_options_popup__straightforward -> listener.onOptionStudyStraightforward(item)
                R.id.decks_study_options_popup__random -> listener.onOptionStudyRandom(item)
                R.id.decks_study_options_popup__newest_first -> listener.onOptionNewestFirst(item)
                R.id.decks_study_options_popup__study_more -> listener.onOptionStudyMore(item)
                R.id.deck_study_options_popup__details -> listener.onOptionDetails(item)
            }
            true
        }
        menu.show()
    }

    override fun onItemClicked(item: DeckListItem) {
        listener.onDeckItemClicked(item)
    }
}

interface DeckListAdapterListener {
    fun onDeckItemClicked(deck: DeckListItem)
    fun onOptionStudyStraightforward(deck: DeckListItem)
    fun onOptionStudyRandom(deck: DeckListItem)
    fun onOptionNewestFirst(deck: DeckListItem)
    fun onOptionStudyMore(deck: DeckListItem)
    fun onOptionDetails(deck: DeckListItem)
}

private fun CardType.toTypeStringRes() = when (this) {
    CardType.FORWARD -> R.string.decks__type__passive
    CardType.REVERSE -> R.string.decks__type__active
}

class DeckListDeckViewHolder(
        val views: DeckListItemBinding,
        private val callback: Callback
) : FlexListViewHolder.EntityItem<DeckListItem>(views.root) {

    override fun bind(item: FlexListItem.EntityItem<DeckListItem>) {
        val context = views.deckListItemText.context
        val deck = item.entity

        views.deckListItemText.text = deck.deck.name
        if (deck.cardTypeFilter == CardTypeFilter.BOTH) {
            views.deckListItemType.visible = false
        } else {
            views.deckListItemType.text = deck.cardTypeFilter.toCardType().toTypeStringRes().run { context.getString(this) }
        }
        views.deckListItemMore.isClickable = true
        views.deckListItemMore.setOnClickListener { callback.showPopupMenu(deck, it) }
        itemView.setOnClickListener { callback.onItemClicked(deck) }
        views.pendingIndicator.visible = deck.hasPending
    }

    interface Callback {
        fun showPopupMenu(item: DeckListItem, anchor: View)
        fun onItemClicked(item: DeckListItem)
    }
}