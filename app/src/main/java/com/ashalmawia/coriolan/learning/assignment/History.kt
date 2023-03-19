package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.model.Card

interface History {

    fun canGoBack(): Boolean

    fun record(card: CardWithState)

    fun forget(card: Card)

    fun goBack(): CardWithState
}

interface HistoryFactory {

    fun create(): History
}

object HistoryFactoryImpl : HistoryFactory {

    override fun create() = SimpleHistory()
}