package com.ashalmawia.coriolan.ui.learning

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.exercise.flashcards.FlashcardsAnswer
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.ui.commons.setOnSingleClickListener
import com.ashalmawia.coriolan.ui.view.visible
import kotlinx.android.synthetic.main.card_translation_item.view.*
import kotlinx.android.synthetic.main.card_view.view.*

private const val BUTTON_BAR_ANIMATION_DURATION = 200L

class CardView : FrameLayout {

    lateinit var listener: CardViewListener

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()

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

    fun bind(card: Card, answers: List<FlashcardsAnswer>) {
        frontText.text = card.original.value
        transcriptionText.bindTranscription(card.original.transcription)

        clearTranslationItems()
        card.translations.forEach { addTranslationItem(it) }

        configureButtonsBar(answers)

        showFront()
    }

    private fun configureButtonsBar(answers: List<FlashcardsAnswer>) {
        if (!answers.contains(FlashcardsAnswer.WRONG) || !answers.contains(FlashcardsAnswer.CORRECT)) {
            throw IllegalStateException("no wrong or correct state, unsupported")
        }

        val hasHard = answers.contains(FlashcardsAnswer.HARD)
        buttonHard.visible = hasHard
        buttonHardCover.visible = !hasHard

        val hasEasy = answers.contains(FlashcardsAnswer.EASY)
        buttonEasy.visible = hasEasy
        buttonEasyCover.visible = !hasEasy
    }

    private fun showFront() {
        if (backShown()) {
            hideButtonBarAnimated()
        }
        frontCover.visible = false
        backCover.visible = true
    }

    private fun showBack() {
        backCover.visibility = View.GONE

        showButtonBarAnimated()
    }

    private fun backShown() = !frontCover.visible && !backCover.visible

    private fun showButtonBarAnimated() {
        if (!buttonsBar.visible) {
            buttonsBar.y += buttonsBar.measuredHeight
            buttonsBar.visible = true
        }

        val animator = ObjectAnimator.ofFloat(buttonsBar, "y", buttonsBar.y, buttonsBar.y - buttonsBar.measuredHeight)
        animator.duration = BUTTON_BAR_ANIMATION_DURATION
        animator.start()
    }

    private fun hideButtonBarAnimated() {
        if (!buttonsBar.visible) return

        val animator = ObjectAnimator.ofFloat(buttonsBar, "y", buttonsBar.y, buttonsBar.y + buttonsBar.measuredHeight)
        animator.duration = BUTTON_BAR_ANIMATION_DURATION
        animator.start()
    }

    private fun addTranslationItem(term: Term) {
        val view = LayoutInflater.from(context).inflate(R.layout.card_translation_item, translations, false)
        view.text.text = term.value
        view.transcription.bindTranscription(term.transcription)
        translations.addView(view)
    }

    private fun clearTranslationItems() {
        translations.removeViews(1, translations.childCount - 1)
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