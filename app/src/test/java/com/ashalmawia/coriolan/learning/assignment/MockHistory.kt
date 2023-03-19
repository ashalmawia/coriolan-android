package com.ashalmawia.coriolan.learning.assignment

object MockHistoryFactory : HistoryFactory {

    override fun create(): History = SimpleHistory()
}