package com.ashalmawia.coriolan.ui

import android.app.Activity
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.debug.DebugIncreaseDateDialog

abstract class BaseActivity : AppCompatActivity() {

    protected val appMenu: AppMenu by lazy { AppMenu(this) { DebugIncreaseDateDialog(this) } }
    protected val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }

    protected fun setUpToolbar(title: String, subtitle: String? = null, cancellable: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(cancellable)
        supportActionBar!!.title = title
        supportActionBar!!.subtitle = subtitle
    }

    protected fun setUpToolbar(@StringRes titleRes: Int, @StringRes subtitleRes: Int = 0, cancellable: Boolean = true) {
        val title = getString(titleRes)
        val subtitle = if (subtitleRes == 0) null else getString(subtitleRes)
        setUpToolbar(title, subtitle, cancellable)
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