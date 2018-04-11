package com.ashalmawia.coriolan.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.ui.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setUpToolbar(R.string.settings__title)
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}