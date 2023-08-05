package com.ashalmawia.coriolan.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.DomainActivityBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.commons.showFeatureDiscoverySequence
import com.ashalmawia.coriolan.ui.commons.tapTargetForNavigationIcon
import com.ashalmawia.coriolan.ui.commons.tapTargetForToolbarOverflow
import com.ashalmawia.coriolan.ui.commons.tapTargetForView
import com.ashalmawia.coriolan.ui.domain_add_edit.AddEditDomainActivity
import com.ashalmawia.coriolan.ui.main.decks_list.DecksListFragment
import com.ashalmawia.coriolan.ui.main.edit.EditFragment
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsFragment
import com.ashalmawia.coriolan.ui.util.requireSerializable
import com.ashalmawia.errors.Errors
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import org.koin.android.ext.android.inject

private enum class Tab {
    DECKS_LIST,
    EDIT,
    STATISTICS
}

private const val FRAGMENT_DECKS_LIST = "fragment_decks_list"
private const val FRAGMENT_EDIT = "fragment_edit"
private const val FRAGMENT_STATISTICS = "fragment_statistics"

private const val EXTRA_DOMAIN_ID = "domain_id"

private const val KEY_SELECTED_TAB = "selected_tag"
private const val TAG = "DomainActivity"

class DomainActivity : BaseActivity() {

    private val views by lazy { DomainActivityBinding.inflate(layoutInflater) }

    private val repository: Repository by inject()
    private val preferences: Preferences by inject()

    private val domainId: DomainId by lazy { intent.requireSerializable(EXTRA_DOMAIN_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        if (!intent.hasExtra(EXTRA_DOMAIN_ID)) {
            throw IllegalStateException("missing domain id")
        }

        setUpBottomBarNavigation()
    }

    override fun onStart() {
        super.onStart()
        refreshAndBindDomain()
    }

    private fun refreshAndBindDomain() {
        val domain = repository.domainById(domainId)!!
        setUpToolbar(domain.name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_TAB, views.bottomNavigation.currentItem)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        views.bottomNavigation.currentItem = savedInstanceState.getInt(KEY_SELECTED_TAB)
    }

    private fun setUpBottomBarNavigation() {
        views.apply {
            val adapter = AHBottomNavigationAdapter(this@DomainActivity, R.menu.domain_navigation_bar)
            adapter.setupWithBottomNavigation(bottomNavigation)

            bottomNavigation.defaultBackgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
            bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            bottomNavigation.setOnTabSelectedListener { position, _ -> onNavigationItemSelected(position) }
            selectTab(Tab.DECKS_LIST)
        }
    }

    private fun selectTab(tab: Tab) {
        views.bottomNavigation.currentItem = tab.ordinal
    }

    private fun onNavigationItemSelected(position: Int): Boolean {
        return when (position) {
            Tab.DECKS_LIST.ordinal -> {
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
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_DECKS_LIST)
                ?: DecksListFragment.create(domainId)
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_DECKS_LIST)
                .commitAllowingStateLoss()
    }

    private fun switchToEdit() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_EDIT)
                ?: EditFragment.create(domainId)
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_EDIT)
                .commitAllowingStateLoss()
    }

    private fun switchToStatistics() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_STATISTICS)
                ?: StatisticsFragment.create(domainId)
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_STATISTICS)
                .commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.domain, menu)
        return appMenu.onCreateOptionsMenu(menuInflater, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                openDomainEditing()
                true
            }
            else -> appMenu.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
        }
    }

    fun onDecksListFragmentInflated(firstDeckView: View) {
        if (!preferences.isMainFeatureDiscoverySeen()) {
            showMainFeatureDiscovery(firstDeckView)
        }
    }

    private fun showMainFeatureDiscovery(firstDeckView: View) {
        val editTab = views.bottomNavigation.getViewAtPosition(1)

        showFeatureDiscoverySequence(listOf(
                tapTargetForView(firstDeckView, R.string.feature_discovery_deck_title, R.string.feature_discovery_deck_description),
                tapTargetForView(editTab, R.string.feature_discovery_edit_title, R.string.feature_discovery_edit_description),
                tapTargetForNavigationIcon(toolbar, R.string.feature_discovery_back_title, R.string.feature_discovery_back_description),
                tapTargetForToolbarOverflow(toolbar, R.string.feature_discovery_overflow_title, R.string.feature_discovery_overflow_description)
        )) { preferences.recordMainFeatureDiscoverySeen() }
    }

    private fun openDomainEditing() {
        val intent = AddEditDomainActivity.edit(this, domainId)
        startActivity(intent)
    }

    companion object {

        fun intent(context: Context, domain: Domain): Intent {
            val intent = Intent(context, DomainActivity::class.java)
            intent.putExtra(EXTRA_DOMAIN_ID, domain.id)
            return intent
        }
    }
}