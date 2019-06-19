package com.ashalmawia.coriolan.ui.view

import android.animation.ObjectAnimator
import kotlinx.android.synthetic.main.card_view.view.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.ashalmawia.coriolan.learning.SRAnswer
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.ui.commons.setOnSingleClickListener

private const val BUTTON_BAR_ANIMATION_DURATION = 200L

class CardView : FrameLayout {

    lateinit var listener: CardViewListener

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

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

    fun bind(card: Card, answers: List<SRAnswer>) {
        frontText.text = card.original.value
        backText.text = translationsToString(card.translations)
        configureButtonsBar(answers)

        showFront()
    }

    private fun configureButtonsBar(answers: List<SRAnswer>) {
        if (!answers.contains(SRAnswer.WRONG) || !answers.contains(SRAnswer.CORRECT)) {
            throw IllegalStateException("no wrong or correct state, unsupported")
        }

        val hasHard = answers.contains(SRAnswer.HARD)
        buttonHard.visible = hasHard
        buttonHardCover.visible = !hasHard

        val hasEasy = answers.contains(SRAnswer.EASY)
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

    private fun translationsToString(translations: List<Expression>): String {
        return if (translations.size == 1) {
            // if it's only one translation, we'll show it
            translations[0].value
        } else {
            // for multiple translations, let's concatenate
            val builder = StringBuilder()
            for ((i, value) in translations.withIndex()) {
                builder.append("${i + 1}. ${value.value}\n")
            }
            builder.toString()
        }
    }
}

interface CardViewListener {

    fun onEasy()

    fun onCorrect()

    fun onHard()

    fun onWrong()
}