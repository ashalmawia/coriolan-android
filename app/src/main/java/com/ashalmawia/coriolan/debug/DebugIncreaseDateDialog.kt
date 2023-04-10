package com.ashalmawia.coriolan.debug

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DebugIncreaseDateBinding
import com.ashalmawia.coriolan.learning.TodayManager
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class DebugIncreaseDateDialog(activity: Activity) : Dialog(activity) {

    private val views by lazy { DebugIncreaseDateBinding.inflate(layoutInflater) }

    private val format = DateTimeFormat.mediumDate().withLocale(Locale.ENGLISH)

    private var date = TodayManager.today()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        initialize()
    }

    private fun initialize() {
        newDate(date)

        views.apply {
            debugIncreaseDateDown.setOnClickListener { decrementDate() }
            debugIncreaseDateUp.setOnClickListener { incrementDate() }

            debugIncreaseDateCancel.setOnClickListener { cancel() }
            debugIncreaseDateApply.setOnClickListener { apply() }
        }
    }

    private fun decrementDate() {
        newDate(date.minusDays(1))
    }

    private fun incrementDate() {
        newDate(date.plusDays(1))
    }

    private fun newDate(date: DateTime) {
        this.date = date
        views.debugIncreaseDateDate.text = format.print(date)
    }

    private fun apply() {
        TodayManager.overrideToday(date)
        dismiss()
    }
}