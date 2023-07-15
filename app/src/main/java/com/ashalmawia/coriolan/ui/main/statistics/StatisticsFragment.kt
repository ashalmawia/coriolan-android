package com.ashalmawia.coriolan.ui.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayManager.today
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelCardsByLearningProgress.setupCardsByLearningProgressPanel
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelLineChart.setUpLineChart
import com.ashalmawia.coriolan.ui.view.setOnItemSelectedListener
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

private const val ARGUMENT_DOMAIN_ID = "domain_id"

private const val POSITION_WEEK = 0
private const val POSITION_MONTH = 1
private const val POSITION_YEAR = 2

class StatisticsFragment : BaseFragment() {

    companion object {
        fun create(domain: Domain): StatisticsFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domain.id)
            }
            return StatisticsFragment().also { it.arguments = arguments }
        }
    }

    private lateinit var views: StatisticsBinding

    private val repository: Repository by inject()
    private val logbook: Logbook by inject()
    private val today = today()

    private val domain: Domain by lazy {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        repository.domainById(domainId)!!
    }
    private val allDecks: List<Deck> by lazy {
        repository.allDecks(domain)
    }
    private val allCards: List<Card> by lazy {
        repository.allCards(domain)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        views = StatisticsBinding.inflate(layoutInflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpDateRangePicker()
        initializePanels()
    }

    private fun initializePanels() {
        setupCardsByLearningProgressPanel()
        setUpCardsLearntByDayPanel()
        setUpCardsAddedByDayPanel()
    }

    private fun setupCardsByLearningProgressPanel() {
        val data = extractCardByLearningProgressData()
        views.setupCardsByLearningProgressPanel(data)
    }

    private fun setUpCardsLearntByDayPanel() {
        val range = selectedDateRange()
        val data = extractCardsLearntByDayData(range.from, range.to)
        views.cardsStudiedByDay.setUpLineChart(range.from, range.to, data, R.color.statistics_learnt)
    }

    private fun setUpCardsAddedByDayPanel() {
        val range = selectedDateRange()
        val data = extractCardsAddedByDayData()
        views.cardsAddedByDay.setUpLineChart(range.from, range.to, data, R.color.statistics_in_progress)
    }

    private fun extractCardsLearntByDayData(from: DateTime, to: DateTime): Map<DateTime, Int> {
        val data = logbook.cardsStudiedOnDateRange(from, to, allDecks)
        return data.mapValues { pair ->
            pair.value.filter {
                it.key == CardAction.NEW_CARD_FIRST_SEEN || it.key == CardAction.CARD_REVIEWED
            }.values.sum()
        }
    }

    private fun extractCardsAddedByDayData(): Map<DateTime, Int> {
        return allCards.groupBy { it.dateAdded }.mapValues { it.value.size }
    }

    private fun extractCardByLearningProgressData(): Map<Status, Int> {
        val learningProgress = repository.getProgressForCardsWithOriginals(allCards.map { it.id })
        return learningProgress.values.groupBy { it.status }.mapValues { it.value.count() }.toSortedMap()
    }

    private fun setUpDateRangePicker() {
        val options = listOf(
                // shall be in line with the POSITION_* constants
                R.string.statistics_date_range__week,
                R.string.statistics_date_range__month,
                R.string.statistics_date_range__year
        )
        val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                options.map { requireContext().getString(it) })
        views.dateRangePicker.apply {
            this.adapter = adapter
            setSelection(0)
            setOnItemSelectedListener {
                initializePanels()
            }
        }
    }

    private fun selectedDateRange(): DateRange {
        val selectedPosition = views.dateRangePicker.selectedItemPosition
        val dateFrom = when (selectedPosition) {
            POSITION_WEEK -> today.minusWeeks(1)
            POSITION_MONTH -> today.minusMonths(1)
            POSITION_YEAR -> today.minusYears(1)
            else -> throw IllegalStateException("unsupported selection: $selectedPosition")
        }
        return DateRange(dateFrom, today)
    }
}

private data class DateRange(val from: DateTime, val to: DateTime)