package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.dependencies.domainScope
import kotlinx.android.synthetic.main.app_toolbar.*
import org.kodein.di.KodeinAware

abstract class BaseActivity : AppCompatActivity(), KodeinAware {

    override val kodein by org.kodein.di.android.kodein()

    protected fun setUpToolbar(title: String, cancellable: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(cancellable)
        supportActionBar!!.title = title
    }

    protected fun setUpToolbar(@StringRes titleRes: Int, cancellable: Boolean = true) {
        val title = getString(titleRes)
        setUpToolbar(title, cancellable)
    }

    protected fun setUpToolbarWithLogo() {
        setSupportActionBar(toolbar)
        toolbar.setLogo(R.drawable.ic_logo_action_bar_with_text)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    protected fun finishOk() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    protected fun decksRegistry(): DecksRegistry = DecksRegistry.get(applicationContext, domainScope().get())
}