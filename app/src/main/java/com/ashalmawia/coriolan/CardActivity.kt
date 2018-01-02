package com.ashalmawia.coriolan

import android.content.Context

import kotlinx.android.synthetic.main.card_activity.*
import kotlinx.android.synthetic.main.app_toolbar.*

import android.content.Intent

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ashalmawia.coriolan.learning.FlowListener
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.ui.CardView
import com.ashalmawia.coriolan.ui.CardViewListener

class CardActivity : AppCompatActivity(), CardViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_activity)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        bindToCurrent()
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

    override fun onCorrect() {
        flow().done(this)
    }

    override fun onWrong() {
        flow().reschedule(this)
    }

    private fun bindToCurrent() {
        val view = cardView as CardView
        view.bind(flow().current())
        view.listener = this
    }

    private fun flow() = LearningFlow.current!!

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, CardActivity::class.java)
        }
    }
}
