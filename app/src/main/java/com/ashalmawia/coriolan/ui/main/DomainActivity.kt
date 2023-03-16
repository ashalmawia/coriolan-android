package com.ashalmawia.coriolan.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.dependencies.closeScope
import com.ashalmawia.coriolan.dependencies.createDomainScope
import com.ashalmawia.coriolan.dependencies.createScope
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.main.decks_list.DecksListFragment
import com.ashalmawia.coriolan.ui.main.edit.EditFragment
import com.ashalmawia.coriolan.ui.main.edit.EditFragmentListener
import com.ashalmawia.coriolan.ui.main.statistics.StatisticsFragment
import com.ashalmawia.errors.Errors
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import kotlinx.android.synthetic.main.domain_activity.*
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

class DomainActivity : BaseActivity(), EditFragmentListener {

    private val TAG = DomainActivity::class.java.simpleName

    private val repository: Repository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.domain_activity)

        if (!intent.hasExtra(EXTRA_DOMAIN_ID)) {
            throw IllegalStateException("missing domain id")
        }

        createScope()

        val domainId = intent.getLongExtra(EXTRA_DOMAIN_ID, -1)
        val domain = repository.domainById(domainId)

        domain?.apply {
            createDomainScope(this)
            setUpToolbar(this.name)
        }

        setUpBottomBarNavigation()
    }

    override fun onDestroy() {
        closeScope()
        super.onDestroy()
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
        selectTab(Tab.DECKS_LIST)
    }

    private fun selectTab(tab: Tab) {
        bottomNavigation.currentItem = tab.ordinal
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
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_DECKS_LIST) ?: DecksListFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, fragment, FRAGMENT_DECKS_LIST)
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

    override fun onCreateOptionsMenu(menu: Menu) = appMenu.onCreateOptionsMenu(menuInflater, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return appMenu.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onDataUpdated() {
        selectTab(Tab.DECKS_LIST)
    }

    companion object {

        fun intent(context: Context, domain: Domain): Intent {
            val intent = Intent(context, DomainActivity::class.java)
            intent.putExtra(EXTRA_DOMAIN_ID, domain.id)
            return intent
        }
    }
}