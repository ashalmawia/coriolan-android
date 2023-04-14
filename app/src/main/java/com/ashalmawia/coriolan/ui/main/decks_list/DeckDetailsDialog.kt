package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.stats.DeckStats
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.DeckDetailsBinding
import com.ashalmawia.coriolan.databinding.DeckDetailsSectionBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter

class DeckDetailsDialog(
        activity: Activity,
        deck: Deck,
        repository: Repository
) : Dialog(activity, R.style.Coriolan_Theme_Dialog) {

    private val container: ViewGroup

    init {
        setTitle(deck.name)
        val views = DeckDetailsBinding.inflate(layoutInflater)
        container = views.container
        setContentView(views.root)

        val stats = repository.deckStats(deck)
        fillInfo(stats)
    }

    private fun fillInfo(data: Map<CardTypeFilter, DeckStats>) {
        addCategory(R.string.deck_details_summary, data[CardTypeFilter.BOTH]!!)
        addCategory(R.string.deck_details_passive_vocabulary, data[CardTypeFilter.FORWARD]!!)
        addCategory(R.string.deck_details_active_vocabulary, data[CardTypeFilter.REVERSE]!!)
    }

    private fun addCategory(@StringRes title: Int, stats: DeckStats) {
        DeckDetailsSectionBinding.inflate(layoutInflater, container, true).apply {
            sectionTitle.setText(title)
            cellNew.text = stats.new.toString()
            cellInProgress.text = stats.inProgress.toString()
            cellLearnt.text = stats.learnt.toString()
            cellTotal.text = stats.total.toString()
        }
    }
}