package com.ashalmawia.coriolan.ui.learning

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.ashalmawia.coriolan.databinding.CardTranslationItemBinding
import com.ashalmawia.coriolan.databinding.CardViewBinding
import com.ashalmawia.coriolan.databinding.CardViewButtonBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.ui.commons.setOnSingleClickListener
import com.ashalmawia.coriolan.ui.view.layoutInflator
import com.ashalmawia.coriolan.ui.view.visible

private const val BUTTON_BAR_ANIMATION_DURATION = 200L
private const val BUTTON_RIPLLE_ANIMATION_DURATION = 150L

@SuppressLint("ViewConstructor")    // not intended to be used by tools
class CardView(
        context: Context,
        private val config: CardViewConfiguration,
        private val listener: CardViewListener
) : FrameLayout(context) {

    private val views: CardViewBinding = CardViewBinding.inflate(layoutInflator, this)
    private val buttons: Map<CardViewAnswer, CardViewButtonBinding>

    init {
        views.apply {
            frontCover.setOnSingleClickListener { showBack(true) }
            backCover.setOnSingleClickListener { showBack(true) }

            buttons = initializeButtons(config.buttons)
        }
    }

    private fun initializeButtons(buttons: List<CardViewButton>): Map<CardViewAnswer, CardViewButtonBinding> {
        views.buttonsContainer.rowCount = (buttons.size + 1) / 2
        return buttons.associate { Pair(it.answer, inflateButton(it)) }
    }

    fun bind(card: Card, answers: List<CardViewAnswer>) {
        views.frontText.text = card.original.value
        views.transcriptionText.bindTranscription(card.original.transcription)

        clearTranslationItems()
        card.translations.forEach { addTranslationItem(it) }

        configureButtonsBar(answers)

        showFront()
        if (config.alwaysOpen) {
            showBack(false)
        }
    }

    private fun configureButtonsBar(answers: List<CardViewAnswer>) {
        buttons.forEach { (answer, binding) ->
            val available = answers.contains(answer)
            binding.button.visible = available
            binding.buttonCover.visible = !available
        }
    }

    private fun showFront() {
        if (backShown() && !config.alwaysOpen) {
            hideButtonBarAnimated()
        }
        views.apply {
            frontCover.visible = false
            backCover.visible = true
        }
    }

    private fun showBack(animated: Boolean) {
        views.backCover.visibility = View.GONE

        if (animated) {
            showButtonBarAnimated()
        } else {
            views.buttonsBar.visible = true
        }
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

    private fun inflateButton(config: CardViewButton): CardViewButtonBinding {
        val binding = CardViewButtonBinding.inflate(layoutInflator, views.buttonsContainer, true)
        return binding.apply {
            button.setText(config.textRes)
            button.setBackgroundResource(config.type.backgroundRes)
            button.setTextColor(context.getColor(config.type.textColorRes))
            button.setOnSingleClickListenerWithDelay { listener.onAnswered(config.answer) }

            val feedbackView = when (config.type) {
                CardViewButton.Type.POSITIVE -> views.touchFeedbackPositive
                CardViewButton.Type.NEGATIVE -> views.touchFeedbackNegative
                CardViewButton.Type.NEUTRAL -> views.touchFeedbackNeutral
            }
            feedbackView.addAnchor(button)
        }
    }
}

interface CardViewListener {

    fun onAnswered(answer: CardViewAnswer)
}

private fun View.setOnSingleClickListenerWithDelay(listener: (View) -> Unit) {
    setOnSingleClickListener {
        postDelayed({ listener(it) }, BUTTON_RIPLLE_ANIMATION_DURATION)
    }
}