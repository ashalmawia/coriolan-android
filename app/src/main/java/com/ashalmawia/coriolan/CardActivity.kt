package com.ashalmawia.coriolan

import android.content.Context
import kotlinx.android.synthetic.main.card_activity.*

import android.content.Intent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ashalmawia.coriolan.learning.FlowListener
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.ui.CardView
import com.ashalmawia.coriolan.ui.FlipListener

class CardActivity : AppCompatActivity(), FlipListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_activity)

        bindToCurrent()

        buttonYes.setOnClickListener { correct() }
        buttonNo.setOnClickListener { wrong() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        bindToCurrent()
    }

    override fun onStart() {
        super.onStart()

        flow().listener = object : FlowListener {
            override fun onFinish() {
                finish()
            }
        }
    }

    override fun onStop() {
        super.onStop()

        LearningFlow.current?.listener = null
    }

    private fun correct() {
        flow().done(this)
    }

    private fun wrong() {
        flow().reschedule(this)
    }

    private fun bindToCurrent() {
        val view = cardView as CardView
        view.bind(flow().current())
        view.flippedListener = this

        setButtonsBarVisibility(View.INVISIBLE)
    }

    override fun onFlipped() {
        setButtonsBarVisibility(View.VISIBLE)
    }

    private fun setButtonsBarVisibility(visibility: Int) {
        buttonsBar.visibility = visibility
    }

    private fun flow() = LearningFlow.current!!

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CardActivity::class.java)
        }
    }
}
