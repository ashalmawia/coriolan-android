package com.ashalmawia.coriolan.model

data class PendingCardsCount(
        val forward: Int,
        val reverse: Int
) {
    val total = forward + reverse
}