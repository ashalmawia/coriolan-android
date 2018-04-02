package com.ashalmawia.coriolan.learning.scheduler.sr

import com.ashalmawia.coriolan.learning.scheduler.State
import com.ashalmawia.coriolan.learning.scheduler.Status
import com.ashalmawia.coriolan.learning.scheduler.today
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

const val PERIOD_NEVER_SCHEDULED = -1
const val PERIOD_FIRST_ASNWER_WRONG = -2
const val PERIOD_LEARNT = 30 * 4               // 4 months

private val format = DateTimeFormat.forPattern("dd MMM hh:mm").withLocale(Locale.ENGLISH)

data class SRState(
        val due: DateTime,
        val period: Int
) : State {

    override val status: Status
        get() {
            return when (period) {
                PERIOD_NEVER_SCHEDULED -> Status.NEW
                0, PERIOD_FIRST_ASNWER_WRONG -> Status.RELEARN
                in 1 until PERIOD_LEARNT -> Status.IN_PROGRESS
                else /* >= PERIOD_LEARNT */ -> Status.LEARNT
            }
        }


    override fun toString(): String {
        return "due: ${format.print(due)}, period: $period"
    }
}

fun emptyState(): SRState {
    return SRState(today(), PERIOD_NEVER_SCHEDULED)
}