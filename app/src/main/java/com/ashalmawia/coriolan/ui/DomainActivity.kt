package com.ashalmawia.coriolan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.settings.SettingsFragment
import com.ashalmawia.errors.Errors
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import kotlinx.android.synthetic.main.domain_activity.*

private enum class Tab {
    LEARNING,
    EDIT,
    STATISTICS,
    SETTINGS
}

private const val FRAGMENT_LEARNING = "fragment_learning"
private const val FRAGMENT_EDIT = "fragment_edit"
private const val FRAGMENT_STATISTICS = "fragment_statistics"
private const val FRAGMENT_SETTINGS = "fragment_settings"

private const val EXTRA_DOMAIN_ID = "domain_id"

class DomainActivity : BaseActivity(), EditFragmentListener {

    private val TAG = DomainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.domain_activity)

        setUpToolbarWithLogo()

        if (!intent.hasExtra(EXTRA_DOMAIN_ID)) {
            throw IllegalStateException("missing domain id")
        }

        val domainId = intent.getLongExtra(EXTRA_DOMAIN_ID, -1)
        initializeDecksRegistry(domainId)

        setUpBottomBarNavigation()
    }

    private fun initializeDecksRegistry(domainId: Long) {
        val domain = DomainsRegistry.domain() ?: throw IllegalStateException("domain was not initialized")
        // TODO: handle actual domain when we get multiple domains
        DecksRegistry.initialize(this, Preferences.get(this), domain, Repository.get(this))
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
            Tab.SETTINGS.ordinal -> {
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