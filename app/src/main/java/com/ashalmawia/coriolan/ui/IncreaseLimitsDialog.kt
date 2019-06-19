package com.ashalmawia.coriolan.ui

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.today
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.util.orZero
import kotlinx.android.synthetic.main.increase_limits.view.*
import org.joda.time.DateTime
import kotlin.math.max

class IncreaseLimitsDialog(
        private val context: Context,
        private val deck: Deck,
        private val exercise: Exercise<*, *>,
        private val date: DateTime,
        private val repository: Repository,
        private val preferences: Preferences
) {
    private val totalCounts = lazy { repository.deckPendingCounts(exercise.stableId, deck, date) }

    private val builder = AlertDialog.Builder(context)

    fun build() : AlertDialog {
        val view = View.inflate(context, R.layout.increase_limits, null) as ViewGroup

        populateMaxCounts(view)

        builder.setView(view)
        builder.setTitle(context.getString(R.string.deck_options_study_more__title, deck.name))

        builder.setNegativeButton(R.string.button_cancel, null)
        builder.setPositiveButton(R.string.button_ok, null)

        return builder.create().apply {
            setOnShowListener {
                getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener { checkAndConfirm(view, this) }
            }
        }
    }

    private fun populateMaxCounts(view: ViewGroup) {
        view.countNewMax.text = newMax.toString()
        view.countReviewMax.text = reviewMax.toString()
    }

    private val newMax
        get() = max(totalCounts.value.total.new - preferences.getNewCardsDailyLimit(today()).orZero(), 0)

    private val reviewMax
        get() = max(totalCounts.value.total.review - preferences.getReviewCardsDailyLimit(today()).orZero(), 0)

    private fun checkAndConfirm(view: ViewGroup, dialog: DialogInterface) {
        val new = if (view.countNew.text.isBlank()) 0 else view.countNew.text.toString().toInt()
        if (new < 0) {
            showError(R.string.increase_limits__error__wrong_new)
            return
        }

        val review = if (view.countReview.text.isBlank()) 0 else view.countReview.text.toString().toInt()
        if (review < 0) {
            showError(R.string.increase_limits__error__wrong_review)
            return
        }

        confirm(new, review)
        dialog.dismiss()
    }

    private fun showError(@StringRes error: Int) {
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
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