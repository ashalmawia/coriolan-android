package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.model.Card

interface Exercise {

    fun refetchCard(card: Card)

    fun dropCard(card: Card)

    fun showNextOrComplete()
}