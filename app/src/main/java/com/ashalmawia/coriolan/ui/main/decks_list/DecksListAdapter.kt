package com.ashalmawia.coriolan.ui.main.decks_list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DeckListItemBinding
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListAdapter
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListItem
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListViewHolder
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.view.layoutInflator
import com.ashalmawia.coriolan.ui.view.visible

class DecksListAdapter(private val listener: DeckListAdapterListener)
    : BaseDeckListAdapter<DeckListDeckViewHolder, DeckListItem>(), DeckListDeckViewHolder.Callback {

    override fun createDeckViewHolder(context: Context, parent: ViewGroup): DeckListDeckViewHolder {
        val views = DeckListItemBinding.inflate(parent.layoutInflator, parent, false)
        return DeckListDeckViewHolder(views, this)
    }

    override fun showPopupMenu(item: DeckListItem, anchor: View) {
        val menu = PopupMenu(anchor.context, anchor)
        menu.inflate(R.menu.decks_study_options_popup)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.decks_study_options_popup__straightforward -> studyStraightforward(item)
                R.id.decks_study_options_popup__random -> studyRandom(item)
                R.id.decks_study_options_popup__newest_first -> studyNewestFirst(item)
                R.id.decks_study_options_popup__study_more -> studyMore(item)
                R.id.deck_study_options_popup__details -> showDeckDetails(item)
            }
            true
        }
        menu.show()
    }

    override fun studyDefault(item: DeckListItem) {
        instantiateLearningFlow(item, StudyOrder.default())
    }

    private fun studyStraightforward(item: DeckListItem) {
        instantiateLearningFlow(item, StudyOrder.ORDER_ADDED)
    }

    private fun studyRandom(item: DeckListItem) {
        instantiateLearningFlow(item, StudyOrder.RANDOM)
    }

    private fun studyNewestFirst(item: DeckListItem) {
        val studyOrder = StudyOrder.NEWEST_FIRST
        instantiateLearningFlow(item, studyOrder)
    }

    private fun instantiateLearningFlow(item: DeckListItem, studyOrder: StudyOrder) {
        listener.beginStudy(item, studyOrder)
    }

    private fun studyMore(item: DeckListItem) {
        listener.showLearnMoreDialog(item)
    }

    private fun showDeckDetails(item: DeckListItem) {
        listener.showDeckDetailsDialog(item)
    }
}

interface DeckListAdapterListener {
    fun showDeckDetailsDialog(deck: DeckListItem)

    fun showLearnMoreDialog(deck: DeckListItem)

    fun beginStudy(item: DeckListItem, studyOrder: StudyOrder)
}

private fun CardType.toTypeStringRes() = when (this) {
    CardType.FORWARD -> R.string.decks__type__passive
    CardType.REVERSE -> R.string.decks__type__active
}

class DeckListDeckViewHolder(
        val views: DeckListItemBinding,
        private val callback: Callback
) : BaseDeckListViewHolder.DeckItem<DeckListItem>(views.root) {

    override fun bind(item: BaseDeckListItem.DeckItem<DeckListItem>) {
        val context = views.deckListItemText.context
        val deck = item.deck

        views.deckListItemText.text = deck.deck.name
        if (deck.cardTypeFilter == CardTypeFilter.BOTH) {
            views.deckListItemType.visible = false
        } else {
            views.deckListItemType.text = deck.cardTypeFilter.toCardType().toTypeStringRes().run { context.getString(this) }
        }
        views.deckListItemMore.isClickable = true
        views.deckListItemMore.setOnClickListener { callback.showPopupMenu(deck, it) }
        itemView.setOnClickListener { callback.studyDefault(deck) }
        views.pendingIndicator.visible = deck.hasPending
    }

    interface Callback {
        fun showPopupMenu(item: DeckListItem, anchor: View)
        fun studyDefault(item: DeckListItem)
    }
}