package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.State

object MockHistoryFactory : HistoryFactory {

    override fun <T : State> create(): History<T> = SimpleHistory()
}