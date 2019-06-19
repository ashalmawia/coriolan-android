package com.ashalmawia.coriolan.debug

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.learning.overrideToday
import com.ashalmawia.coriolan.learning.today
import kotlinx.android.synthetic.main.debug_increase_date.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.*

class DebugIncreaseDateActivity(context: Context) : Dialog(context) {

    private val format = DateTimeFormat.mediumDate().withLocale(Locale.ENGLISH)

    private var date = today()

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
        overrideToday(date)
        dismiss()
    }

    companion object {
        fun launch(context: Context) {
            val dialog = DebugIncreaseDateActivity(context)
            dialog.show()
        }
    }
}