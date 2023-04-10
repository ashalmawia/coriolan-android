package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Activity
import android.app.Dialog
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.Counts
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.DeckDetailsBinding
import com.ashalmawia.coriolan.databinding.DeckDetailsSectionBinding
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import org.joda.time.DateTime

class DeckDetailsDialog(
        activity: Activity,
        private val deck: Deck,
        private val date: DateTime,
        private val repository: Repository
) : Dialog(activity, R.style.Coriolan_Theme_Dialog) {

    private val container: ViewGroup

    init {
        setTitle(deck.name)
        val views = DeckDetailsBinding.inflate(layoutInflater)
        container = views.container
        setContentView(views.root)
        fillInfo()
    }

    private fun fillInfo() {
        val countsForward = counts(CardType.FORWARD)
        val countsReverse = counts(CardType.REVERSE)
        val countsTotal = countsForward + countsReverse

        addCategory(R.string.deck_details_summary, countsTotal)
        addCategory(R.string.deck_details_passive_vocabulary, countsForward)
        addCategory(R.string.deck_details_active_vocabulary, countsReverse)
    }

    private fun addCategory(@StringRes title: Int, counts: Counts) {
        DeckDetailsSectionBinding.inflate(layoutInflater, container, true).apply {
            sectionTitle.setText(title)
            cellNew.text = counts.new.toString()
            cellReview.text = counts.review.toString()
            cellTotal.text = counts.total.toString()
        }
    }

    private fun counts(cardType: CardType) = repository.deckPendingCounts(deck, cardType, date)

}