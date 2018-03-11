package com.ashalmawia.coriolan.ui.view

import kotlinx.android.synthetic.main.card_view.view.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.ashalmawia.coriolan.learning.scheduler.Answer
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Expression
import com.ashalmawia.coriolan.ui.commons.setOnSingleClickListener

class CardView : LinearLayout {

    lateinit var listener: CardViewListener

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        setOnSingleClickListener { showBack() }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        showAnswerButton.setOnSingleClickListener { showBack() }
        buttonYes.setOnSingleClickListener { listener.onCorrect() }
        buttonNo.setOnSingleClickListener { listener.onWrong() }
        buttonHard.setOnSingleClickListener { listener.onHard() }
        buttonEasy.setOnSingleClickListener { listener.onEasy() }
    }

    fun bind(card: Card, answers: Array<Answer>) {
        front.text = card.original.value
        back.text = translationsToString(card.translations)
        configureButtonsBar(answers)

        showFront()
    }

    private fun configureButtonsBar(answers: Array<Answer>) {
        buttonNo.visible = answers.contains(Answer.WRONG)
        buttonHard.visible = answers.contains(Answer.HARD)
        buttonYes.visible = answers.contains(Answer.CORRECT)
        buttonEasy.visible = answers.contains(Answer.EASY)
    }

    private fun showFront() {
        divider.visibility = View.INVISIBLE
        back.visibility = View.INVISIBLE
        buttonsBar.visibility = View.GONE
        showAnswerButton.visibility = View.VISIBLE
    }

    private fun showBack() {
        divider.visibility = View.VISIBLE
        back.visibility = View.VISIBLE
        buttonsBar.visibility = View.VISIBLE
        showAnswerButton.visibility = View.GONE
    }

    private fun translationsToString(translations: List<Expression>): String {
        return if (translations.size == 1) {
            // if it's only one translation, we'll show it
            translations[0].value
        } else {
            // for multiple translations, let's concatenate
            val builder = StringBuilder()
            for ((i, value) in translations.withIndex()) {
                builder.append("${i+1}. ${value.value}\n")
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