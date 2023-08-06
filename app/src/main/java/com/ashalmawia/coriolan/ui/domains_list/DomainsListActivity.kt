package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.databinding.DomainsListBinding
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.viewModelBuilder
import org.koin.android.ext.android.get

class DomainsListActivity : BaseActivity() {

    companion object {
        fun intent(context: Context) = Intent(context, DomainsListActivity::class.java)
    }

    private val viewModel by viewModelBuilder { DomainsListViewModel(get(), get()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val views = DomainsListBinding.inflate(layoutInflater)
        setContentView(views.root)

        val view: DomainsListView = DomainsListViewImpl(views, this, viewModel)
        viewModel.init(view)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu) = appMenu.onCreateOptionsMenu(menuInflater, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return appMenu.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}