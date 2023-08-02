package com.ashalmawia.coriolan.ui.learning

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R

data class CardViewConfiguration(
        val buttons: List<CardViewButton>,
        val alwaysOpen: Boolean,
        val showNewBadge: Boolean
) {

    class Builder {
        private val buttons = mutableListOf<CardViewButton>()
        private var alwaysOpen: Boolean? = null
        private var showNewBadge: Boolean? = null

        fun addButton(
                @StringRes textRes: Int,
                type: CardViewButton.Type,
                answer: CardAnswer
        ): Builder {
            buttons.add(CardViewButton(textRes, type, answer))
            return this
        }

        fun alwaysOpen(value: Boolean): Builder {
            alwaysOpen = value
            return this
        }

        fun showNewBadge(value: Boolean): Builder {
            showNewBadge = value
            return this
        }

        fun build(): CardViewConfiguration {
            val open = alwaysOpen ?: throw IllegalStateException("always open must be set")
            if (buttons.isEmpty()) throw IllegalStateException("at least one button must be added")
            if (buttons.size > 4) throw IllegalStateException("CardView can have max 4 buttons")
            val showNewBadge = showNewBadge ?: false

            return CardViewConfiguration(buttons.toList(), open, showNewBadge)
        }
    }
}

data class CardViewButton(
        @StringRes val textRes: Int,
        val type: Type,
        val answer: CardAnswer
) {
    enum class Type(
            @ColorRes val textColorRes: Int,
            @DrawableRes val backgroundRes: Int
    ) {
        POSITIVE(textColorRes = R.color.white, backgroundRes = R.drawable.answer_correct_bg),
        NEGATIVE(textColorRes = R.color.white, backgroundRes = R.drawable.answer_wrong_bg),
        NEUTRAL(textColorRes = R.color.colorPrimary, backgroundRes = R.drawable.answer_additional_bg)
    }
}