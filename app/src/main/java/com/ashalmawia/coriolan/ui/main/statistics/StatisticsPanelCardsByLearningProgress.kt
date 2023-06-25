package com.ashalmawia.coriolan.ui.main.statistics

import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.StatisticsBinding
import com.ashalmawia.coriolan.learning.Status
import ir.mahozad.android.PieChart
import ir.mahozad.android.unit.Dimension

object StatisticsPanelCardsByLearningProgress {

    fun StatisticsBinding.setupCardsByLearningProgressPanel(data: Map<Status, Int>) {
        val total = data.values.sum()

        statisticsCardsByProgress.apply {
            panelCardsByLearningProgress.apply {
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

            bindLegendLabel(Status.NEW, cardsByLearningProgressLegendNew, data)
            bindLegendLabel(Status.RELEARN, cardsByLearningProgressLegendForgot, data)
            bindLegendLabel(Status.IN_PROGRESS, cardsByLearningProgressLegendInProgress, data)
            bindLegendLabel(Status.LEARNT, cardsByLearningProgressLegendLearnt, data)

            cardsByLearningProgressLegendTotal.text = total.toString()
        }
    }

    private fun bindLegendLabel(status: Status, label: TextView, data: Map<Status, Int>) {
        label.text = data[status].toString()
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