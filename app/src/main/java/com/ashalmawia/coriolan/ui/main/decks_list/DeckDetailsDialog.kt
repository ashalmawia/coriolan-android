package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Activity
import android.app.Dialog
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.exercise.Exercise
import kotlinx.android.synthetic.main.deck_details.*
import org.joda.time.DateTime

class DeckDetailsDialog(
        activity: Activity,
        private val deck: DeckListItem,
        private val exercise: Exercise<*, *>,
        private val date: DateTime,
        private val repository: Repository
) : Dialog(activity, R.style.Coriolan_Theme_Dialog) {

    init {
        setTitle(deck.deck.name)
        setContentView(R.layout.deck_details)
        fillInfo()
    }

    private fun fillInfo() {
        val counts = counts()

        cellNew.text = counts.new.toString()
        cellReview.text = counts.review.toString()
        cellTotal.text = counts.total.toString()
    }

    private fun counts() = repository.deckPendingCounts(exercise.stableId, deck.deck, deck.cardType, date)

}