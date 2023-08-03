package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.DomainsListBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.domain_add_edit.CreateDomainActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import org.koin.android.ext.android.inject

class DomainsListActivity : BaseActivity() {

    private val views by lazy { DomainsListBinding.inflate(layoutInflater) }

    private val repository: Repository by inject()

    companion object {
        fun intent(context: Context) = Intent(context, DomainsListActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)

        setUpToolbarWithLogo()
    }

    override fun onResume() {
        super.onResume()

        setUpList()
    }

    private fun setUpList() {
        val domains = repository.allDomains()

        views.domainsList.apply {
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

    override fun onCreateOptionsMenu(menu: Menu) = appMenu.onCreateOptionsMenu(menuInflater, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return appMenu.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
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