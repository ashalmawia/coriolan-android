package com.ashalmawia.coriolan.ui

import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.app_toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    protected fun setUpToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = title
    }

    protected fun setUpToolbar(@StringRes titleRes: Int) {
        val title = getString(titleRes)
        setUpToolbar(title)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}