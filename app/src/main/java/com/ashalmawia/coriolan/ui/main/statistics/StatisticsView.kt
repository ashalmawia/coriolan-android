package com.ashalmawia.coriolan.ui.main.statistics

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelCardsByLearningProgress.setupCardsByLearningProgressPanel
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelLineChart.setUpLineChart
import com.ashalmawia.coriolan.ui.view.setOnItemSelectedListener

private const val POSITION_WEEK = 0
private const val POSITION_MONTH = 1
private const val POSITION_YEAR = 2

interface StatisticsView

class StatisticsViewImpl(
        private val views: StatisticsBinding,
        private val fragment: StatisticsFragment,
        private val viewModel: StatisticsViewModel
) : StatisticsView {
    private val context = views.root.context

    init {
        setUpDateRangePicker()
        viewModel.currentState.observe(fragment) {
            render(it)
        }
    }

    private fun setUpDateRangePicker() {
        val options = listOf(
                // shall be in line with the POSITION_* constants
                R.string.statistics_date_range__week,
                R.string.statistics_date_range__month,
                R.string.statistics_date_range__year
        )
        val adapter = ArrayAdapter(
                context,
                android.R.layout.simple_spinner_dropdown_item,
                options.map { context.getString(it) })
        views.dateRangePicker.apply {
            this.adapter = adapter
            setSelection(0)
            setOnItemSelectedListener {
                viewModel.onDateRangeChanged(selectedDateRange())
            }
        }
    }

    private fun selectedDateRange(): StatisticsDateRange {
        val selectedPosition = views.dateRangePicker.selectedItemPosition
        return when (selectedPosition) {
            POSITION_WEEK -> StatisticsDateRange.LastWeek
            POSITION_MONTH -> StatisticsDateRange.LastMonth
            POSITION_YEAR -> StatisticsDateRange.LastYear
            else -> throw IllegalStateException("unsupported selection: $selectedPosition")
        }
    }

    private fun StatisticsDateRange.toPosition() = when (this) {
        StatisticsDateRange.LastWeek -> POSITION_WEEK
        StatisticsDateRange.LastMonth -> POSITION_MONTH
        StatisticsDateRange.LastYear -> POSITION_YEAR
    }

    private fun render(state: StatisticsViewState) {
        when (state) {
            is StatisticsViewState.Loading -> renderLoading()
            is StatisticsViewState.Data -> renderData(state)
        }
    }

    private fun renderLoading() {
        fragment.showLoading()
        setUpChartBackgrounds(true)
    }

    private fun setUpChartBackgrounds(isLoading: Boolean) {
        views.apply {
            statisticsCardsByProgress.panelCardsByLearningProgress.visibility =
                    if (isLoading) View.INVISIBLE else View.VISIBLE
            statisticsCardsByProgress.panelPlaceholder.isVisible = isLoading
        }
    }

    private fun renderData(state: StatisticsViewState.Data) {
        fragment.hideLoading()
        setUpChartBackgrounds(false)

        views.setupCardsByLearningProgressPanel(state.learningProgressData)
        views.cardsStudiedByDay.setUpLineChart(state.rangeFrom, state.rangeTo, state.cardsLearntByDayData, R.color.statistics_learnt)
        views.cardsAddedByDay.setUpLineChart(state.rangeFrom, state.rangeTo, state.cardsAddedByDayData, R.color.statistics_in_progress)

        views.dateRangePicker.apply {
            val dateListener = onItemSelectedListener
            onItemSelectedListener = null
            setSelection(state.rangeValue.toPosition())
            onItemSelectedListener = dateListener
        }
    }
}