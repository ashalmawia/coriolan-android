package com.ashalmawia.coriolan.ui

import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import com.ashalmawia.coriolan.R
import com.ashalmawia.errors.Errors
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.domain_activity.*

private const val TAB_LEARNING = 0
private const val TAB_EDIT = 1
private const val TAB_STATISTICS = 2
private const val TAB_SETTINGS = 3

private const val FRAGMENT_LEARNING = "fragment_learning"
private const val FRAGMENT_EDIT = "fragment_edit"
private const val FRAGMENT_STATISTICS = "fragment_statistics"
private const val FRAGMENT_SETTINGS = "fragment_settings"

class DomainActivity : AppCompatActivity() {

    private val TAG = DomainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.domain_activity)

        setSupportActionBar(toolbar)
        toolbar.setLogo(R.drawable.ic_logo_action_bar_with_text)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        setUpBottomBarNavigation()
    }

    private fun setUpBottomBarNavigation() {
        val adapter = AHBottomNavigationAdapter(this, R.menu.domain_navigation_bar)
        adapter.setupWithBottomNavigation(bottomNavigation)

        bottomNavigation.defaultBackgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.setOnTabSelectedListener { position, _ -> onNavigationItemSelected(position) }
        bottomNavigation.currentItem = TAB_LEARNING
    }

    private fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            TAB_LEARNING -> {
                switchToLearning()
                true
            }
            TAB_EDIT -> {
                switchToEdit()
                true
            }
            TAB_STATISTICS -> {
                switchToStatistics()
                true
            }
            TAB_SETTINGS -> {
                switchToSettings()
                true
            }
            else -> {
                Errors.illegalState(TAG, "unknown option[$position]")
                false
            }
        }
    }

    private fun switchToLearning() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_LEARNING) ?: LearningFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_LEARNING)
                .commit()
    }

    private fun switchToEdit() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_EDIT) ?: EditFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_EDIT)
                .commit()
    }

    private fun switchToStatistics() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_STATISTICS) ?: StatisticsFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_STATISTICS)
                .commit()
    }

    private fun switchToSettings() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_SETTINGS) ?: SettingsFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_SETTINGS)
                .commit()
    }
}