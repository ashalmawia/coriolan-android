package com.ashalmawia.coriolan.data.stats

data class DeckStats(val new: Int, val inProgress: Int, val learnt: Int) {

    val total = new + inProgress + learnt
}

data class MutableDeckStats(var new: Int = 0, var inProgress: Int = 0, var learnt: Int = 0) {

    fun toDeckStats() = DeckStats(new, inProgress, learnt)
}