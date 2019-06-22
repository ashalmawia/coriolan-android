package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.app.Dialog
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.model.Deck
import kotlinx.android.synthetic.main.deck_details.*
import org.joda.time.DateTime

class DeckDetailsDialog(
        activity: Activity,
        private val deck: Deck,
        private val exercise: Exercise<*, *>,
        private val date: DateTime,
        private val repository: Repository
) : Dialog(activity) {

    init {
        setTitle(deck.name)
        setContentView(R.layout.deck_details)
        fillInfo()
    }

    private fun fillInfo() {
        val counts = counts()

        cellNewForward.text = counts.forward.new.toString()
        cellReviewForward.text = counts.forward.review.toString()
        cellTotalForward.text = counts.forward.total.toString()

        cellNewReverse.text = counts.reverse.new.toString()
        cellReviewReverse.text = counts.reverse.review.toString()
        cellTotalReverse.text = counts.reverse.total.toString()
    }

    private fun counts() = repository.deckPendingCounts(exercise.stableId, deck, date)

}