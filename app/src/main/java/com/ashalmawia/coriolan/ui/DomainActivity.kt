package com.ashalmawia.coriolan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.BuildConfig
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.debug.DebugIncreaseDateActivity
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.edit.EditFragment
import com.ashalmawia.coriolan.ui.edit.EditFragmentListener
import com.ashalmawia.coriolan.ui.settings.SettingsActivity
import com.ashalmawia.errors.Errors
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import kotlinx.android.synthetic.main.domain_activity.*

private enum class Tab {
    LEARNING,
    EDIT,
    STATISTICS
}

private const val FRAGMENT_LEARNING = "fragment_learning"
private const val FRAGMENT_EDIT = "fragment_edit"
private const val FRAGMENT_STATISTICS = "fragment_statistics"

private const val EXTRA_DOMAIN_ID = "domain_id"

private const val KEY_SELECTED_TAB = "selected_tag"

class DomainActivity : BaseActivity(), EditFragmentListener {

    private val TAG = DomainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.domain_activity)

        if (!intent.hasExtra(EXTRA_DOMAIN_ID)) {
            throw IllegalStateException("missing domain id")
        }

        val domainId = intent.getLongExtra(EXTRA_DOMAIN_ID, -1)
        val repository = Repository.get(this)
        val domain = repository.domainById(domainId)

        domain?.apply {
            setUpToolbar(domain.name)
        }

        setUpBottomBarNavigation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_TAB, bottomNavigation.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bottomNavigation.currentItem = savedInstanceState.getInt(KEY_SELECTED_TAB)
    }

    private fun setUpBottomBarNavigation() {
        val adapter = AHBottomNavigationAdapter(this, R.menu.domain_navigation_bar)
        adapter.setupWithBottomNavigation(bottomNavigation)

        bottomNavigation.defaultBackgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.setOnTabSelectedListener { position, _ -> onNavigationItemSelected(position) }
        selectTab(Tab.LEARNING)
    }

    private fun selectTab(tab: Tab) {
        bottomNavigation.currentItem = tab.ordinal
    }

    private fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            Tab.LEARNING.ordinal -> {
                switchToLearning()
                true
            }
            Tab.EDIT.ordinal -> {
                switchToEdit()
                true
            }
            Tab.STATISTICS.ordinal -> {
                switchToStatistics()
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
                .commitAllowingStateLoss()
    }

    private fun switchToEdit() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_EDIT) ?: EditFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_EDIT)
                .commitAllowingStateLoss()
    }

    private fun switchToStatistics() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_STATISTICS) ?: StatisticsFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_STATISTICS)
                .commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.domain, menu)

        if (BuildConfig.DEBUG) {
            menu.setGroupVisible(R.id.menu_group_debug, true)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_debug_increase_date -> {
                increaseDate()
                true
            }
            R.id.menu_settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun increaseDate() {
        DebugIncreaseDateActivity.launch(this)
    }

    private fun openSettings() {
        val intent = SettingsActivity.intent(this)
        startActivity(intent)
    }

    override fun onDataUpdated() {
        selectTab(Tab.LEARNING)
    }

    companion object {

        fun intent(context: Context, domain: Domain): Intent {
            val intent = Intent(context, DomainActivity::class.java)
            intent.putExtra(EXTRA_DOMAIN_ID, domain.id)
            return intent
        }
    }
}