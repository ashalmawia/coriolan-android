package com.ashalmawia.coriolan.ui.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.TodayManager.today
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelCardsByLearningProgress.setupCardsByLearningProgressPanel
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelCardsStudiedByDate.setUpCardsLearntByDayPanel
import org.joda.time.DateTime
import org.koin.android.ext.android.inject

private const val ARGUMENT_DOMAIN_ID = "domain_id"

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        views = StatisticsBinding.inflate(layoutInflater, container, false)
        return views.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCardsByLearningProgressPanel()
        setUpCardsLearntByDayPanel()
    }

    private fun setupCardsByLearningProgressPanel() {
        val data = extractCardByLearningProgressData()
        views.setupCardsByLearningProgressPanel(data)
    }

    private fun setUpCardsLearntByDayPanel() {
        val from = today.minusDays(7)
        val to = today
        val data = extractCardsLearntByDayData(from, to)
        views.setUpCardsLearntByDayPanel(from, to, data)
    }

    private fun extractCardsLearntByDayData(from: DateTime, to: DateTime): Map<DateTime, Int> {
        val data = logbook.cardsStudiedOnDateRange(from, to)
        return data.mapValues { pair ->
            pair.value.filter {
                it.key == CardAction.NEW_CARD_FIRST_SEEN || it.key == CardAction.CARD_REVIEWED
            }.values.sum()
        }
    }

    private fun extractCardByLearningProgressData(): Map<Status, Int> {
        val allCards = repository.allCards(domain)
        val learningProgress = repository.getProgressForCardsWithOriginals(allCards.map { it.id })
        return learningProgress.values.groupBy { it.status }.mapValues { it.value.count() }.toSortedMap()
    }
}