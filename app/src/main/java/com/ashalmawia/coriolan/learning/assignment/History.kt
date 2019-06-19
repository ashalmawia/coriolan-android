package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.CardWithState
import com.ashalmawia.coriolan.learning.State

interface History<T : State> {

    fun canGoBack(): Boolean

    fun record(card: CardWithState<T>)

    fun goBack(): CardWithState<T>

    companion object {
        fun <T : State> create() = SimpleHistory<T>()
    }
}