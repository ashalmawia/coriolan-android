package com.ashalmawia.coriolan.ui.learning

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.ashalmawia.coriolan.databinding.CardTranslationItemBinding
import com.ashalmawia.coriolan.databinding.CardViewBinding
import com.ashalmawia.coriolan.learning.exercise.flashcards.FlashcardsAnswer
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.ui.commons.setOnSingleClickListener
import com.ashalmawia.coriolan.ui.view.layoutInflator
import com.ashalmawia.coriolan.ui.view.visible

private const val BUTTON_BAR_ANIMATION_DURATION = 200L

class CardView(context: Context, private val listener: CardViewListener) : FrameLayout(context) {

    private val views: CardViewBinding = CardViewBinding.inflate(layoutInflator, this)

    init {
        views.apply {
            frontCover.setOnSingleClickListener { showBack() }
            backCover.setOnSingleClickListener { showBack() }

            buttonYes.setOnSingleClickListener { listener.onCorrect() }
            buttonNo.setOnSingleClickListener { listener.onWrong() }

            buttonHard.setOnSingleClickListener { listener.onHard() }
            buttonEasy.setOnSingleClickListener { listener.onEasy() }

            touchFeedbackWrong.addAnchor(buttonNo)
            touchFeedbackCorrect.addAnchor(buttonYes)
            touchFeedbackAdditional.addAnchor(buttonEasy, buttonHard)
        }
    }

    fun bind(card: Card, answers: List<FlashcardsAnswer>) {
        views.frontText.text = card.original.value
        views.transcriptionText.bindTranscription(card.original.transcription)

        clearTranslationItems()
        card.translations.forEach { addTranslationItem(it) }

        configureButtonsBar(answers)

        showFront()
    }

    private fun configureButtonsBar(answers: List<FlashcardsAnswer>) {
        if (!answers.contains(FlashcardsAnswer.WRONG) || !answers.contains(FlashcardsAnswer.CORRECT)) {
            throw IllegalStateException("no wrong or correct state, unsupported")
        }

        views.apply {
            val hasHard = answers.contains(FlashcardsAnswer.HARD)
            buttonHard.visible = hasHard
            buttonHardCover.visible = !hasHard

            val hasEasy = answers.contains(FlashcardsAnswer.EASY)
            buttonEasy.visible = hasEasy
            buttonEasyCover.visible = !hasEasy
        }
    }

    private fun showFront() {
        if (backShown()) {
            hideButtonBarAnimated()
        }
        views.apply {
            frontCover.visible = false
            backCover.visible = true
        }
    }

    private fun showBack() {
        views.backCover.visibility = View.GONE

        showButtonBarAnimated()
    }

    private fun backShown() = !views.frontCover.visible && !views.backCover.visible

    private fun showButtonBarAnimated() {
        views.apply {
            if (!buttonsBar.visible) {
                buttonsBar.y += buttonsBar.measuredHeight
                buttonsBar.visible = true
            }

            val animator = ObjectAnimator.ofFloat(
                    buttonsBar, "y", buttonsBar.y, buttonsBar.y - buttonsBar.measuredHeight)
            animator.duration = BUTTON_BAR_ANIMATION_DURATION
            animator.start()
        }
    }

    private fun hideButtonBarAnimated() {
        views.apply {
            if (!buttonsBar.visible) return

            val animator = ObjectAnimator.ofFloat(
                    buttonsBar, "y", buttonsBar.y, buttonsBar.y + buttonsBar.measuredHeight)
            animator.duration = BUTTON_BAR_ANIMATION_DURATION
            animator.start()
        }
    }

    private fun addTranslationItem(term: Term) {
        views.apply {
            val cardTranslationItem = CardTranslationItemBinding.inflate(layoutInflator, translations, false)
            cardTranslationItem.text.text = term.value
            cardTranslationItem.transcription.bindTranscription(term.transcription)
            translations.addView(cardTranslationItem.root)
        }
    }

    private fun clearTranslationItems() {
        views.translations.removeViews(1, views.translations.childCount - 1)
    }

    private fun TextView.bindTranscription(transcription: String?) {
        text = transcription
        visible = !transcription.isNullOrBlank()
    }
}

interface CardViewListener {

    fun onEasy()

    fun onCorrect()

    fun onHard()

    fun onWrong()
}