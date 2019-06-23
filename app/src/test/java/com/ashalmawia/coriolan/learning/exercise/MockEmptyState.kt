package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.exercise.sr.PERIOD_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.exercise.sr.SRState

fun mockEmptySRState(today: LearningDay) = SRState(today, PERIOD_NEVER_SCHEDULED)

class MockEmptyStateProvider(private val today: LearningDay) : EmptyStateProvider {
    override fun emptySRState() = mockEmptySRState(today)
}