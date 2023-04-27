package com.ashalmawia.coriolan.ui.main.decks_list

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DeckListItemBinding
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.ui.view.layoutInflator
import com.ashalmawia.coriolan.ui.view.visible
import com.ashalmawia.coriolan.util.inflate

private const val TAG = "DecksListAdapter"

private const val TYPE_HEADER = 1
private const val TYPE_ITEM = 2

class DecksListAdapter(private val listener: DeckListAdapterListener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val decks: MutableList<DeckListItem> = mutableListOf()

    fun setData(data: List<DeckListItem>) {
        decks.clear()
        decks.addAll(data)

        val timeStart = System.currentTimeMillis()
        Log.d(TAG, "time spend for loading decks states: ${System.currentTimeMillis() - timeStart} ms")

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return decks.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_HEADER else TYPE_ITEM
    }

    private fun positionToIndex(position: Int): Int = position - 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_HEADER) {
            // skip
            return
        }

        holder as DeckViewHolder
        val item = decks[positionToIndex(position)]

        val context = holder.views.deckListItemText.context

        holder.views.deckListItemText.text = item.deck.name
        if (item.cardTypeFilter == CardTypeFilter.BOTH) {
            holder.views.deckListItemType.visible = false
        } else {
            holder.views.deckListItemType.text = item.cardTypeFilter.toCardType().toTypeStringRes().run { context.getString(this) }
        }
        holder.views.deckListItemMore.isClickable = true
        holder.views.deckListItemMore.setOnClickListener { showPopupMenu(item, it) }
        holder.itemView.setOnClickListener { studyDefault(item) }
        holder.views.pendingIndicator.visible = item.hasPending
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            createHeaderViewHolder(parent)
        } else {
            createItemViewHolder(parent)
        }
    }

    private fun createHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return HeaderViewHolder(parent.inflate(R.layout.learning_list_header, false))
    }

    private fun createItemViewHolder(parent: ViewGroup): DeckViewHolder {
        val views = DeckListItemBinding.inflate(parent.layoutInflator, parent, false)
        return DeckViewHolder(views)
    }

    private fun showPopupMenu(item: DeckListItem, anchor: View) {
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

    private fun studyDefault(item: DeckListItem) {
        studyRandom(item)
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
        listener.showIncreaseLimitsDialog(item)
    }

    private fun showDeckDetails(item: DeckListItem) {
        listener.showDeckDetailsDialog(item)
    }
}

interface DeckListAdapterListener {
    fun showDeckDetailsDialog(deck: DeckListItem)

    fun showIncreaseLimitsDialog(deck: DeckListItem)

    fun beginStudy(item: DeckListItem, studyOrder: StudyOrder)
}

private fun CardType.toTypeStringRes() = when (this) {
    CardType.FORWARD -> R.string.decks__type__passive
    CardType.REVERSE -> R.string.decks__type__active
}