package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.Card

interface History {

    fun canGoBack(): Boolean

    fun record(task: Task)

    fun forget(card: Card)

    fun goBack(): Task
}

interface HistoryFactory {

    fun create(): History
}

object HistoryFactoryImpl : HistoryFactory {

    override fun create() = SimpleHistory()
}