package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.State
import com.ashalmawia.coriolan.learning.exercise.sr.PERIOD_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.exercise.sr.SRState
import com.ashalmawia.coriolan.learning.mockToday

fun mockEmptyState(today: LearningDay = mockToday()) = State(mockEmptySRState(today))
fun mockEmptySRState(today: LearningDay) = SRState(today, PERIOD_NEVER_SCHEDULED)

class MockEmptyStateProvider(private val today: LearningDay) : EmptyStateProvider {
    override fun emptyState() = State(emptySRState())

    override fun emptySRState() = mockEmptySRState(today)
}