package com.ashalmawia.coriolan.ui

import kotlinx.android.synthetic.main.card_view.view.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Expression

class CardView : LinearLayout {

    lateinit var listener: CardViewListener

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        setOnClickListener { showBack() }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        showAnswerButton.setOnClickListener { showBack() }
        buttonYes.setOnClickListener { listener.onCorrect() }
        buttonNo.setOnClickListener { listener.onWrong() }
    }

    fun bind(card: Card) {
        front.text = card.original.value
        back.text = translationsToString(card.translations)

        showFront()
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

    fun onCorrect()

    fun onWrong()
}