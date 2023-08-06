package com.ashalmawia.coriolan.ui.main

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DomainActivityBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.commons.showFeatureDiscoverySequence
import com.ashalmawia.coriolan.ui.commons.tapTargetForNavigationIcon
import com.ashalmawia.coriolan.ui.commons.tapTargetForToolbarOverflow
import com.ashalmawia.coriolan.ui.commons.tapTargetForView
import com.ashalmawia.coriolan.ui.domain_add_edit.AddEditDomainActivity
import com.ashalmawia.coriolan.ui.domain_add_edit.DeleteDomainDialog.showConfirmDeleteDomainDialog
import com.ashalmawia.coriolan.ui.main.decks_list.DecksListFragment
import com.ashalmawia.coriolan.ui.main.edit.EditFragment
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsFragment
import com.ashalmawia.errors.Errors
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter

private const val FRAGMENT_DECKS_LIST = "fragment_decks_list"
private const val FRAGMENT_EDIT = "fragment_edit"
private const val FRAGMENT_STATISTICS = "fragment_statistics"

private const val TAG = "DomainView"

interface DomainView {
    val selectedTabIndex: Int

    fun init()
    fun bind(domain: Domain)
    fun showMainFeatureDiscovery(firstDeckView: View)
    fun onCreateOptionsMenu(menu: Menu)
    fun onOptionsItemSelected(item: MenuItem): Boolean
    fun selectTab(index: Int)
    fun finish()

    fun showLoading()
}

class DomainViewImpl(
        private val views: DomainActivityBinding,
        private val activity: BaseActivity,
        private val viewModel: DomainViewModel
) : DomainView {

    private val supportFragmentManager = activity.supportFragmentManager

    override val selectedTabIndex: Int
        get() = views.bottomNavigation.currentItem

    override fun init() {
        activity.setContentView(views.root)
        setUpBottomBarNavigation()
    }

    private fun setUpBottomBarNavigation() {
        views.apply {
            val adapter = AHBottomNavigationAdapter(activity, R.menu.domain_navigation_bar)
            adapter.setupWithBottomNavigation(bottomNavigation)

            bottomNavigation.defaultBackgroundColor = ResourcesCompat.getColor(
                    activity.resources, R.color.colorPrimary, null)
            bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            bottomNavigation.setOnTabSelectedListener { position, _ -> onNavigationItemSelected(position) }
        }
    }

    override fun bind(domain: Domain) {
        activity.setUpToolbar(domain.name)
        selectTab(Tab.DECKS_LIST)
    }

    override fun finish() {
        activity.hideLoading()
        activity.finish()
    }

    override fun selectTab(index: Int) {
        views.bottomNavigation.currentItem = index
    }
    private fun selectTab(tab: Tab) {
        selectTab(tab.ordinal)
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
                ?: DecksListFragment.create(domainId())
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_DECKS_LIST)
                .commitAllowingStateLoss()
    }

    private fun switchToEdit() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_EDIT)
                ?: EditFragment.create(domainId())
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_EDIT)
                .commitAllowingStateLoss()
    }

    private fun switchToStatistics() {
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_STATISTICS)
                ?: StatisticsFragment.create(domainId())
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_STATISTICS)
                .commitAllowingStateLoss()
    }

    override fun showMainFeatureDiscovery(firstDeckView: View) {
        val editTab = views.bottomNavigation.getViewAtPosition(1)

        activity.showFeatureDiscoverySequence(listOf(
                tapTargetForView(firstDeckView, R.string.feature_discovery_deck_title, R.string.feature_discovery_deck_description),
                tapTargetForView(editTab, R.string.feature_discovery_edit_title, R.string.feature_discovery_edit_description),
                tapTargetForNavigationIcon(activity.toolbar, R.string.feature_discovery_back_title, R.string.feature_discovery_back_description),
                tapTargetForToolbarOverflow(activity.toolbar, R.string.feature_discovery_overflow_title, R.string.feature_discovery_overflow_description)
        )) { viewModel.onMainFeatureDiscoveryDismissed() }
    }

    private fun openDomainEditing() {
        val intent = AddEditDomainActivity.edit(activity, domainId())
        activity.startActivity(intent)
    }

    private fun domainId() = viewModel.domain.id

    private fun confirmDeleteDomain() {
        activity.showConfirmDeleteDomainDialog(viewModel.domain) { viewModel.deleteDomain() }
    }

    override fun onCreateOptionsMenu(menu: Menu) {
        activity.menuInflater.inflate(R.menu.domain, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit -> {
                openDomainEditing()
                true
            }
            R.id.menu_delete -> {
                confirmDeleteDomain()
                true
            }
            else -> false
        }
    }

    override fun showLoading() {
        activity.showLoading()
    }

    private fun hideLoading() {
        activity.hideLoading()
    }
}

private enum class Tab {
    DECKS_LIST,
    EDIT,
    STATISTICS
}