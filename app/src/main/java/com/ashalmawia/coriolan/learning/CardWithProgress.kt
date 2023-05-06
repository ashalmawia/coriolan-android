package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.model.Card

data class CardWithProgress(val card: Card, val learningProgress: LearningProgress) {
    val status: Status = learningProgress.globalStatus
}

fun  List<CardWithProgress>.new() = this.filter { it.status == Status.NEW }
fun  List<CardWithProgress>.review() = this.filter { it.status != Status.NEW }