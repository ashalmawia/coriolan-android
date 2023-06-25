package com.ashalmawia.coriolan.ui.main.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import ir.mahozad.android.PieChart
import ir.mahozad.android.unit.Dimension
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

        setupCardsByLearningProgressPanel()
    }

    private fun setupCardsByLearningProgressPanel() {
        val data = extractCardByLearningProgressData()
        val total = data.values.sum()

        views.panelCardsByLearningProgress.apply {
            slices = data.map {
                val fraction = it.value / total.toFloat()
                PieChart.Slice(
                    fraction = fraction,
                    color = ResourcesCompat.getColor(resources, it.key.color(), null)
                )
            }
            holeRatio = 0f
            overlayRatio = 0f

            labelType = PieChart.LabelType.OUTSIDE
            labelsColor = resources.getColor(R.color.textColorPrimary, null)
            labelsFont = ResourcesCompat.getFont(context, R.font.font_montserrat_bold)!!
            labelsSize = 12.dp
            outsideLabelsMargin = 4.dp
        }

        bindLegendLabel(Status.NEW, views.cardsByLearningProgressLegendNew, data)
        bindLegendLabel(Status.RELEARN, views.cardsByLearningProgressLegendForgot, data)
        bindLegendLabel(Status.IN_PROGRESS, views.cardsByLearningProgressLegendInProgress, data)
        bindLegendLabel(Status.LEARNT, views.cardsByLearningProgressLegendLearnt, data)

        views.cardsByLearningProgressLegendTotal.text = total.toString()
    }

    private fun bindLegendLabel(status: Status, label: TextView, data: Map<Status, Int>) {
        label.text = data[status].toString()
    }

    private fun extractCardByLearningProgressData(): Map<Status, Int> {
        val allCards = repository.allCards(domain)
        val learningProgress = repository.getProgressForCardsWithOriginals(allCards.map { it.id })
        return learningProgress.values.groupBy { it.status }.mapValues { it.value.count() }.toSortedMap()
    }

    @ColorRes
    private fun Status.color(): Int {
        return when (this) {
            Status.NEW -> R.color.statistics_new
            Status.RELEARN -> R.color.statistics_forgot
            Status.IN_PROGRESS -> R.color.statistics_in_progress
            Status.LEARNT -> R.color.statistics_learnt
        }
    }
}

private val Int.dp: Dimension
    get() = Dimension.DP(this.toFloat())