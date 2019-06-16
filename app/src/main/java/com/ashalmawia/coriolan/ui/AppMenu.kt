package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.ashalmawia.coriolan.BuildConfig
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.debug.DebugIncreaseDateActivity
import com.ashalmawia.coriolan.ui.settings.SettingsActivity

object AppMenu {

    fun onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean {
        menuInflater.inflate(R.menu.domain, menu)

        if (BuildConfig.DEBUG) {
            menu.setGroupVisible(R.id.menu_group_debug, true)
        }

        return true
    }

    fun onOptionsItemSelected(activity: Activity, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_debug_increase_date -> {
                increaseDate(activity)
                true
            }
            R.id.menu_settings -> {
                openSettings(activity)
                true
            }
            else -> false
        }
    }

    private fun increaseDate(activity: Activity) {
        DebugIncreaseDateActivity.launch(activity)
    }

    private fun openSettings(activity: Activity) {
        val intent = SettingsActivity.intent(activity)
        activity.startActivity(intent)
    }
}