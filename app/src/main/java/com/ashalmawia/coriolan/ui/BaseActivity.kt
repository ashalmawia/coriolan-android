package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.dependencies.createScope
import kotlinx.android.synthetic.main.app_toolbar.*
import org.koin.core.scope.Scope

abstract class BaseActivity : AppCompatActivity() {

    private var scope: Scope? = null

    protected val navigator: Navigator
        get() = requireScope().get()

    protected val appMenu: AppMenu
        get() = requireScope().get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope = createScope()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope?.close()
        scope = null
    }

    fun requireScope() = scope!!

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

    protected fun navigateBack() {
        super.onBackPressed()
    }
}