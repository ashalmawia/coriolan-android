package com.ashalmawia.coriolan.learning.scheduler

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

const val PERIOD_NEVER_SCHEDULED = -1
const val PERIOD_LEARNT = 30 * 4               // 4 months

private val format = DateTimeFormat.forPattern("dd MMM hh:mm").withLocale(Locale.ENGLISH)

data class State(
        val due: DateTime,
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
        return "due: ${format.print(due)}, period: $period"
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