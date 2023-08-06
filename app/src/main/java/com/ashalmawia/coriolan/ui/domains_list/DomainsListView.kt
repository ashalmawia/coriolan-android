package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DomainsListBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.domain_add_edit.AddEditDomainActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity

interface DomainsListView {
    fun showDomains(domains: List<Domain>)
}

class DomainsListViewImpl(
        private val views: DomainsListBinding,
        private val activity: BaseActivity
) : DomainsListView {

    init {
        activity.setUpToolbarWithLogo()
        views.domainsList.apply {
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    override fun showDomains(domains: List<Domain>) {
        views.domainsList.apply {
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
        activity.startActivity(intent)
    }

    private fun subtitleItem() = DomainsListItem.CategoryItem(R.string.domains_list__subtitle)

    private fun createNewDomainItem() = DomainsListItem.OptionItem(
            R.string.domains_list__add,
            R.drawable.ic_add,
            this::createNewDomain
    )

    private fun createNewDomain(context: Context) {
        val intent = AddEditDomainActivity.create(context, false)
        activity.startActivity(intent)
    }
}