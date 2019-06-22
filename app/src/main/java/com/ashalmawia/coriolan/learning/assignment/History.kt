package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State

interface History<T : State> {

    fun canGoBack(): Boolean

    fun record(card: CardWithState<T>)

    fun goBack(): CardWithState<T>
}

interface HistoryFactory {

    fun <T : State> create(): History<T>
}

object HistoryFactoryImpl : HistoryFactory {

    override fun <T : State> create() = SimpleHistory<T>()
}