package com.ashalmawia.coriolan.ui.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsPanelCardsByLearningProgress.setupCardsByLearningProgressPanel
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

        val cardsByLearningProgress = extractCardByLearningProgressData()
        views.setupCardsByLearningProgressPanel(cardsByLearningProgress)
    }

    private fun extractCardByLearningProgressData(): Map<Status, Int> {
        val allCards = repository.allCards(domain)
        val learningProgress = repository.getProgressForCardsWithOriginals(allCards.map { it.id })
        return learningProgress.values.groupBy { it.status }.mapValues { it.value.count() }.toSortedMap()
    }
}