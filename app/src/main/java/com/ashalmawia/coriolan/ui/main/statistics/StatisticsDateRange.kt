package com.ashalmawia.coriolan.ui.main.statistics

sealed class StatisticsDateRange {

    object LastWeek : StatisticsDateRange()
    object LastMonth : StatisticsDateRange()
    object LastYear : StatisticsDateRange()
    // potentially: custom date range

}
