package com.ashalmawia.coriolan.ui

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.ashalmawia.coriolan.BuildConfig
import com.ashalmawia.coriolan.R

class AppMenu(private val navigator: Navigator) {

    fun onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean {
        menuInflater.inflate(R.menu.domain, menu)

        if (BuildConfig.DEBUG) {
            menu.setGroupVisible(R.id.menu_group_debug, true)
        }

        return true
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_debug_increase_date -> {
                increaseDate()
                true
            }
            R.id.menu_settings -> {
                openSettings()
                true
            }
            else -> false
        }
    }

    private fun increaseDate() {
        navigator.createDebugIncreaseDateDialog().show()
    }

    private fun openSettings() {
        navigator.openSettings()
    }
}