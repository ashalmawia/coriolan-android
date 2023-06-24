package com.ashalmawia.coriolan.model

data class PendingCardsCount(
        val forward: Counts,
        val reverse: Counts
) {
    val total = forward + reverse
}