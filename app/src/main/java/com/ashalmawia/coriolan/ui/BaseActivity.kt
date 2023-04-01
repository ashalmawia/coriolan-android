package com.ashalmawia.coriolan.ui

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.debug.DebugIncreaseDateDialog
import kotlinx.android.synthetic.main.app_toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    protected val appMenu: AppMenu by lazy { AppMenu(this) { DebugIncreaseDateDialog(this) } }

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