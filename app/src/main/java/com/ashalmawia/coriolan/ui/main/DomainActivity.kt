package com.ashalmawia.coriolan.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.ashalmawia.coriolan.databinding.DomainActivityBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.requireSerializable
import com.ashalmawia.coriolan.ui.util.viewModelBuilder
import org.koin.android.ext.android.get

private const val EXTRA_DOMAIN_ID = "domain_id"

private const val KEY_SELECTED_TAB = "selected_tag"

class DomainActivity : BaseActivity() {

    private val viewModel by viewModelBuilder {
        val domainId = intent.requireSerializable<DomainId>(EXTRA_DOMAIN_ID)
        DomainViewModel(get(), get(), get(), domainId)
    }
    private val view: DomainView by lazy {
        val views = DomainActivityBinding.inflate(layoutInflater)
        DomainViewImpl(views, this, viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view.init()
        viewModel.init(view)
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_TAB, view.selectedTabIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        view.selectTab(savedInstanceState.getInt(KEY_SELECTED_TAB))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        view.onCreateOptionsMenu(menu)
        return appMenu.onCreateOptionsMenu(menuInflater, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return view.onOptionsItemSelected(item)
                || appMenu.onOptionsItemSelected(item)
                || super.onOptionsItemSelected(item)
    }

    fun onDecksListFragmentInflated(firstDeckView: View) {
        viewModel.onDecksListFragmentInflated(firstDeckView)
    }

    companion object {
        fun intent(context: Context, domain: Domain): Intent {
            val intent = Intent(context, DomainActivity::class.java)
            intent.putExtra(EXTRA_DOMAIN_ID, domain.id)
            return intent
        }
    }
}