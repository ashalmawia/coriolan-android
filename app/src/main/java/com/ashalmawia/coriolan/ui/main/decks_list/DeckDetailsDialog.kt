package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Activity
import android.app.Dialog
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.DeckDetailsBinding
import org.joda.time.DateTime

class DeckDetailsDialog(
        activity: Activity,
        private val deck: DeckListItem,
        private val date: DateTime,
        private val repository: Repository
) : Dialog(activity, R.style.Coriolan_Theme_Dialog) {

    private val views by lazy { DeckDetailsBinding.inflate(layoutInflater) }

    init {
        setTitle(deck.deck.name)
        setContentView(views.root)
        views.fillInfo()
    }

    private fun DeckDetailsBinding.fillInfo() {
        val counts = counts()

        cellNew.text = counts.new.toString()
        cellReview.text = counts.review.toString()
        cellTotal.text = counts.total.toString()
    }

    private fun counts() = repository.deckPendingCounts(deck.deck, deck.cardType, date)

}