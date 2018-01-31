package com.ashalmawia.coriolan.learning.scheduler

import java.text.SimpleDateFormat
import java.util.*

const val PERIOD_NEVER_SCHEDULED = -1
const val PERIOD_LEARNT = 30 * 4               // 4 months

private val format = SimpleDateFormat("dd MMM", Locale.ENGLISH)

data class State(
        val due: Date,
        val period: Int
) {

    val status: Status
        get() {
            if (period == PERIOD_NEVER_SCHEDULED) {
                return Status.NEW
            }
            if (period >= PERIOD_LEARNT) {
                return Status.LEARNT
            }
            return Status.IN_PROGRESS
        }


    override fun toString(): String {
        return "due: ${format.format(due)}, period: $period"
    }
}

fun emptyState(): State {
    return State(today(), PERIOD_NEVER_SCHEDULED)
}

enum class Status {
    NEW,
    IN_PROGRESS,
    LEARNT
}