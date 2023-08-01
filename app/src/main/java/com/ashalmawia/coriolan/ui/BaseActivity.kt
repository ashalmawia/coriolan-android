package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.debug.DebugIncreaseDateDialog

abstract class BaseActivity : AppCompatActivity() {

    protected val appMenu: AppMenu by lazy { AppMenu(this) { DebugIncreaseDateDialog(this) } }
    protected val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }

    private val loader: ProgressBar by lazy { findViewById(R.id.toolbar_loading) }

    fun setUpToolbar(title: String, subtitle: String? = null, cancellable: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(cancellable)
        supportActionBar!!.title = title
        supportActionBar!!.subtitle = subtitle
    }

    fun updateToolbarSubtitle(subtitle: String?) {
        supportActionBar!!.subtitle = subtitle
    }

    fun setUpToolbar(@StringRes titleRes: Int, @StringRes subtitleRes: Int = 0, cancellable: Boolean = true) {
        val title = getString(titleRes)
        val subtitle = if (subtitleRes == 0) null else getString(subtitleRes)
        setUpToolbar(title, subtitle, cancellable)
    }

    protected fun setUpToolbarWithLogo() {
        setSupportActionBar(toolbar)
        toolbar.setLogo(R.drawable.ic_logo_action_bar_with_text)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    fun showLoading() {
        loader.isVisible = true
    }

    fun hideLoading() {
        loader.isVisible = false
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