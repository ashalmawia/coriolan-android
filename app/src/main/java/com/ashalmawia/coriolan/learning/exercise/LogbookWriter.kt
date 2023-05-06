package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.SchedulingState
import com.ashalmawia.coriolan.learning.Task

interface LogbookWriter {

    fun recordCardAction(task: Task, newState: SchedulingState)

    fun unrecordCardAction(task: Task, stateThatWasUndone: SchedulingState)
}

enum class CardAction(val value: String) {
    NEW_CARD_FIRST_SEEN("opened"),
    CARD_REVIEWED("reviewed"),
    CARD_RELEARNED("relearned")
}