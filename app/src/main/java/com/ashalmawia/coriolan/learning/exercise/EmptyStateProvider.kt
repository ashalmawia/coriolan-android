package com.ashalmawia.coriolan.learning.exercise

import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.learning.exercise.sr.PERIOD_NEVER_SCHEDULED
import com.ashalmawia.coriolan.learning.exercise.sr.SRState

interface EmptyStateProvider {

    fun emptySRState(): SRState
}

class EmptyStateProviderImpl(private val todayProvider: TodayProvider) : EmptyStateProvider {

    override fun emptySRState(): SRState {
        return SRState(todayProvider.today(), PERIOD_NEVER_SCHEDULED)
    }
}