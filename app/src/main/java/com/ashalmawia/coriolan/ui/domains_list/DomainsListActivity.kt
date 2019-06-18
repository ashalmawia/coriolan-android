package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.AppMenu
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.CreateDomainActivity
import com.ashalmawia.coriolan.ui.DomainActivity
import kotlinx.android.synthetic.main.domains_list.*
import org.kodein.di.generic.instance

class DomainsListActivity : BaseActivity() {

    private val repository: Repository by instance()

    companion object {
        fun intent(context: Context) = Intent(context, DomainsListActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.domains_list)

        setUpToolbarWithLogo()
    }

    override fun onResume() {
        super.onResume()

        setUpList()
    }

    private fun setUpList() {
        val domains = repository.allDomains()

        domainsList.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = DomainsListAdapter(toListItems(domains))
        }
    }

    private fun toListItems(list: List<Domain>): List<DomainsListItem> = mutableListOf<DomainsListItem>()
            .apply {
                add(subtitleItem())
                addAll(list.map { it.toDomainItem() })
                add(createNewDomainItem())
            }

    private fun Domain.toDomainItem()
            = DomainsListItem.DomainItem(this) { context -> openDomain(context, this)}

    private fun openDomain(context: Context, domain: Domain) {
        val intent = DomainActivity.intent(context, domain)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu) = AppMenu.onCreateOptionsMenu(menuInflater, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return AppMenu.onOptionsItemSelected(this, item) || super.onOptionsItemSelected(item)
    }

    private fun subtitleItem() = DomainsListItem.CategoryItem(R.string.domains_list__subtitle)

    private fun createNewDomainItem() = DomainsListItem.OptionItem(
            R.string.domains_list__add,
            R.drawable.ic_add,
            this::createNewDomain
    )

    private fun createNewDomain(context: Context) {
        val intent = CreateDomainActivity.intent(context, false)
        startActivity(intent)
    }
}