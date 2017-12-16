package com.ashalmawia.coriolan.ui

import kotlinx.android.synthetic.main.card_view.view.*

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Expression

class CardView : LinearLayout {

    var flippedListener: FlipListener? = null

    constructor(context: Context?) : this(context, null, 0)
    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        setOnClickListener { showBack() }
    }

    fun bind(card: Card) {
        front.text = card.original.value
        back.text = translationsToString(card.translations)

        showFront()
    }

    private fun showFront() {
        divider.visibility = View.INVISIBLE
        back.visibility = View.INVISIBLE
    }

    private fun showBack() {
        divider.visibility = View.VISIBLE
        back.visibility = View.VISIBLE

        flippedListener?.onFlipped()
    }

    private fun translationsToString(translations: List<Expression>): String {
        if (translations.size == 1) {
            // if it's only one translation, we'll show it
            return translations[0].value
        } else {
            // for multiple translations, let's concatenate
            val builder = StringBuilder()
            for ((i, value) in translations.withIndex()) {
                builder.append(i)
                builder.append(". ")
                builder.append(value.value)
                builder.append("\n")
            }
            return builder.toString()
        }
    }
}

interface FlipListener {

    fun onFlipped()
}