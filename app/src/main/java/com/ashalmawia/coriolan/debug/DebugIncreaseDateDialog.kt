package com.ashalmawia.coriolan.debug

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.TodayManager
import kotlinx.android.synthetic.main.debug_increase_date.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class DebugIncreaseDateDialog(activity: Activity) : Dialog(activity) {

    private val format = DateTimeFormat.mediumDate().withLocale(Locale.ENGLISH)

    private var date = TodayManager.today()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debug_increase_date)
        initialize()
    }

    private fun initialize() {
        newDate(date)

        debug_increase_date__down.setOnClickListener { decrementDate() }
        debug_increase_date__up.setOnClickListener { incrementDate() }

        debug_increase_date__cancel.setOnClickListener { cancel() }
        debug_increase_date__apply.setOnClickListener { apply() }
    }

    private fun decrementDate() {
        newDate(date.minusDays(1))
    }

    private fun incrementDate() {
        newDate(date.plusDays(1))
    }

    private fun newDate(date: DateTime) {
        this.date = date
        debug_increase_date__date.text = format.print(date)
    }

    private fun apply() {
        TodayManager.overrideToday(date)
        dismiss()
    }
}