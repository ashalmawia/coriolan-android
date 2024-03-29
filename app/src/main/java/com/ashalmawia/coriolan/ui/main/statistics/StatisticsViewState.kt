package com.ashalmawia.coriolan.ui.main.statistics

import com.ashalmawia.coriolan.learning.Status
import org.joda.time.DateTime

sealed interface StatisticsViewState {

    data class Data(
            val rangeFrom: DateTime,
            val rangeTo: DateTime,
            val rangeValue: StatisticsDateRange,
            val learningProgressData: Map<Status, Int>,
            val cardsLearntByDayData: Map<DateTime, Int>,
            val cardsAddedByDayData: Map<DateTime, Int>
    ) : StatisticsViewState

    object Loading : StatisticsViewState
}
