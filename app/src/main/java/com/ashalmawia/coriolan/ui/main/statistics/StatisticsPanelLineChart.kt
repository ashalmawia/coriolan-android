package com.ashalmawia.coriolan.ui.main.statistics

import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.util.midnight
import com.ashalmawia.coriolan.util.orZero
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.format.DateTimeFormat

private val dateFormat = DateTimeFormat.forPattern("dd MMM")

object StatisticsPanelLineChart {

    fun LineChart.setUpLineChart(
            from: DateTime,
            to: DateTime,
            data: Map<DateTime, Int>,
            @ColorRes colorRes: Int
    ) {
        val (points, labels) = generateChartData(from, to, data)

        val font = ResourcesCompat.getFont(context, R.font.font_montserrat_bold)
        val formatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                if (value >= labels.size) return "" // avoiding weird MPChart concurrency(?) issues
                return labels[value.toInt()]
            }

            override fun getPointLabel(entry: Entry): String {
                return ""
            }
        }
        apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = formatter
                granularity = 1f
                typeface = font
                yOffset = 10f
                setDrawGridLines(false)
            }
            axisLeft.apply {
                typeface = font
                granularity = 1f
                axisMinimum = 0f
                xOffset = 10f
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false
            isHighlightPerDragEnabled = false
            isHighlightPerTapEnabled = false
            extraBottomOffset = 10f

            val lineDataSet = LineDataSet(points, "").apply {
                lineWidth = 3f
                mode = LineDataSet.Mode.LINEAR
                color = resources.getColor(colorRes, null)
                circleRadius = 3f
                setCircleColor(color)
                setDrawCircleHole(false)
                valueFormatter = formatter
            }
            setData(LineData(lineDataSet))
            invalidate()
        }
    }

    private fun generateChartData(from: DateTime, to: DateTime, data: Map<DateTime, Int>): ChartData {
        val rangeLength = Days.daysBetween(from, to).days
        return if (rangeLength > 31) {
            generateChartDataGroupedByMonth(from, to, data)
        } else {
            generateChartDataDefault(from, to, data)
        }
    }

    private fun generateChartDataGroupedByMonth(from: DateTime, to: DateTime, data: Map<DateTime, Int>): ChartData {
        val dataByMonth = data.entries.groupBy { it.key.monthOfYear() }.mapValues { it.value.sumOf { it.value } }
        val months = mutableListOf<DateTime.Property>()
        var current = to
        while (current > from) {
            months.add(0, current.monthOfYear())
            current = current.minusMonths(1)
        }
        val points = months.mapIndexed { index, month ->
            Entry(index.toFloat(), dataByMonth[month].orZero().toFloat())
        }
        val labels = months.map { it.asShortText }
        return ChartData(points, labels)
    }

    private fun generateChartDataDefault(from: DateTime, to: DateTime, data: Map<DateTime, Int>): ChartData {
        val rangeLength = Days.daysBetween(from, to).days
        val allDates = (0 .. rangeLength).map {
            from.plusDays(it)
        }
        val points = allDates.mapIndexed { index, date ->
            Entry(index.toFloat(), data[date.midnight()].orZero().toFloat())
        }
        val labels = allDates.map { dateFormat.print(it) }
        return ChartData(points, labels)
    }
}

private data class ChartData(val points: List<Entry>, val labels: List<String>)