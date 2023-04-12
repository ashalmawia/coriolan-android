package com.ashalmawia.coriolan.ui.main.decks_list

import android.app.Activity
import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import android.view.ViewGroup
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.IncreaseLimitsBinding
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.util.orZero
import org.joda.time.DateTime
import kotlin.math.max

class IncreaseLimitsDialog(
        private val activity: Activity,
        private val deck: DeckListItem,
        private val date: DateTime,
        private val repository: Repository,
        private val preferences: Preferences
) {
    private lateinit var views: IncreaseLimitsBinding
    private val totalCounts by lazy {
            if (deck.cardTypeFilter == CardTypeFilter.BOTH) {
                repository.deckPendingCountsMix(deck.deck, date)
            } else {
                repository.deckPendingCounts(deck.deck, deck.cardTypeFilter.toCardType(), date)
            }
    }

    private val builder = AlertDialog.Builder(activity)

    fun build() : AlertDialog {
        views = IncreaseLimitsBinding.inflate(activity.layoutInflater)
        val view = views.root as ViewGroup

        populateMaxCounts()

        builder.setView(view)
        builder.setTitle(activity.getString(R.string.deck_options_study_more__title, deck.deck.name))

        builder.setNegativeButton(R.string.button_cancel, null)
        builder.setPositiveButton(R.string.button_ok, null)

        return builder.create().apply {
            setOnShowListener {
                getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { checkAndConfirm(this) }
            }
        }
    }

    private fun populateMaxCounts() {
        views.apply {
            views.countNewMax.text = newMax.toString()
            views.countReviewMax.text = reviewMax.toString()
        }
    }

    private val newMax
        get() = max(totalCounts.new - preferences.getNewCardsDailyLimit(today()).orZero(), 0)

    private val reviewMax
        get() = max(totalCounts.review - preferences.getReviewCardsDailyLimit(today()).orZero(), 0)

    private fun today() = TodayManager.today()

    private fun checkAndConfirm(dialog: DialogInterface) {
        val new = if (views.countNew.text.isBlank()) 0 else views.countNew.text.toString().toInt()
        if (new < 0) {
            showError(R.string.increase_limits__error__wrong_new)
            return
        }

        val review = if (views.countReview.text.isBlank()) 0 else views.countReview.text.toString().toInt()
        if (review < 0) {
            showError(R.string.increase_limits__error__wrong_review)
            return
        }

        confirm(new, review)
        dialog.dismiss()
    }

    private fun showError(@StringRes error: Int) {
        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show()
    }

    private fun confirm(new: Int, review: Int) {
        updateDailyLimits(new, review)
    }

    private fun updateDailyLimits(new: Int, review: Int) {
        preferences.setNewCardsDailyLimit(
                preferences.getNewCardsDailyLimit(date).orZero() + new,
                date
        )
        preferences.setReviewCardsDailyLimit(
                preferences.getReviewCardsDailyLimit(date).orZero() + review,
                date
        )
    }
}