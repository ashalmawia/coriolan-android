package com.ashalmawia.coriolan.ui.learning

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.CardTranslationItemBinding
import com.ashalmawia.coriolan.databinding.CardViewBinding
import com.ashalmawia.coriolan.databinding.CardViewButtonBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.ui.commons.setOnSingleClickListener
import com.ashalmawia.coriolan.ui.util.fixItalicClipping
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
    private val buttons: Map<CardAnswer, CardViewButtonBinding>

    init {
        views.apply {
            backCover.setOnSingleClickListener { showBack(true) }

            buttons = initializeButtons(config.buttons)
        }
    }

    private fun initializeButtons(buttons: List<CardViewButton>): Map<CardAnswer, CardViewButtonBinding> {
        views.buttonsContainer.rowCount = (buttons.size + 1) / 2
        return buttons.associate { Pair(it.answer, inflateButton(it)) }
    }

    fun bind(card: Card, answers: List<CardAnswer>) {
        views.frontText.text = card.original.value
        views.frontText.adjustTextSizeForString(card.original.value)

        views.transcriptionText.bindTranscription(card.original.transcription)

        clearTranslationItems()
        card.translations.forEach { addTranslationItem(it) }

        configureButtonsBar(answers)

        views.badgeNewWord.isVisible = config.showNewBadge

        post {
            showFront()
            if (config.alwaysOpen) {
                showBack(false)
            }
        }
    }

    private fun configureButtonsBar(answers: List<CardAnswer>) {
        buttons.forEach { (answer, binding) ->
            val available = answers.contains(answer)
            binding.button.visible = available
            binding.buttonCover.visible = !available
        }
    }

    private fun showFront() {
        val animate = backShown() && !config.alwaysOpen
        hideButtonBar(animate)
        views.backCover.visible = true
    }

    private fun showBack(animated: Boolean) {
        views.backCover.visible = false
        showButtonBar(animated)
    }

    private fun backShown() = !views.backCover.visible

    private fun showButtonBar(animated: Boolean) {
        views.apply {
            if (animated) {
                val animator = ObjectAnimator.ofFloat(
                        buttonsBar, "translationY", buttonsBar.measuredHeight.toFloat(), 0f)
                animator.duration = BUTTON_BAR_ANIMATION_DURATION
                animator.start()
            } else {
                buttonsBar.translationY = 0f
            }
        }
    }

    private fun hideButtonBar(animated: Boolean) {
        views.apply {
            if (animated) {
                val animator = ObjectAnimator.ofFloat(
                        buttonsBar, "translationY", 0f, buttonsBar.measuredHeight.toFloat())
                animator.duration = BUTTON_BAR_ANIMATION_DURATION
                animator.start()
            } else {
                buttonsBar.translationY = buttonsBar.measuredHeight.toFloat()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addTranslationItem(term: Term) {
        views.apply {
            val cardTranslationItem = CardTranslationItemBinding.inflate(layoutInflator, translations, false)
            cardTranslationItem.text.text = term.value
            cardTranslationItem.text.adjustTextSizeForString(term.value)
            cardTranslationItem.transcription.bindTranscription(term.transcription)
            translations.addView(cardTranslationItem.root)
        }
    }

    private fun clearTranslationItems() {
        views.translations.removeViews(1, views.translations.childCount - 1)
    }

    private fun TextView.bindTranscription(transcription: String?) {
        text = transcription.fixItalicClipping()
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

    private fun TextView.adjustTextSizeForString(string: String) {
        val textSizeRes = if (string.containsHyeroglyphs()) {
            R.dimen.text__glyphs
        } else {
            R.dimen.card_text_size
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(textSizeRes))
    }
}

private fun String.containsHyeroglyphs() = any { Character.isIdeographic(it.code) }

interface CardViewListener {

    fun onAnswered(answer: CardAnswer)
}

private fun View.setOnSingleClickListenerWithDelay(listener: (View) -> Unit) {
    setOnSingleClickListener {
        postDelayed({ listener(it) }, BUTTON_RIPLLE_ANIMATION_DURATION)
    }
}