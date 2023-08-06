package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DomainsListBinding
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.domain_add_edit.AddEditDomainActivity
import com.ashalmawia.coriolan.ui.domain_add_edit.DeleteDomainDialog.showConfirmDeleteDomainDialog
import com.ashalmawia.coriolan.ui.main.DomainActivity

interface DomainsListView {
    fun showLoading()
    fun showDomains(domains: List<Domain>)
}

class DomainsListViewImpl(
        private val views: DomainsListBinding,
        private val activity: BaseActivity,
        private val viewModel: DomainsListViewModel
) : DomainsListView {

    init {
        activity.setUpToolbarWithLogo()
        views.domainsList.apply {
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    override fun showLoading() {
        activity.showLoading()
    }

    override fun showDomains(domains: List<Domain>) {
        activity.hideLoading()
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

    private fun Domain.toDomainItem() = DomainsListItem.DomainItem(
            domain = this,
            onClick = { context -> openDomain(context, this) },
            onMoreClick = { anchor -> showOptionsMenu(this, anchor) }
    )

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

    private fun openEditDomain(domain: Domain) {
        val intent = AddEditDomainActivity.edit(activity, domain.id)
        activity.startActivity(intent)
    }

    private fun confirmDeleteDomain(domain: Domain) {
        activity.showConfirmDeleteDomainDialog(domain) { viewModel.deleteDomain(domain) }
    }

    private fun showOptionsMenu(domain: Domain, anchor: View) {
        val popupMenu = PopupMenu(activity, anchor)
        popupMenu.inflate(R.menu.domains_list_item)
        popupMenu.setOnMenuItemClickListener { item -> onOptionsMenuItemClicked(item, domain) }
        popupMenu.show()
    }

    private fun onOptionsMenuItemClicked(menuItem: MenuItem, domain: Domain): Boolean {
        return when (menuItem.itemId) {
            R.id.menu_edit -> {
                openEditDomain(domain)
                true
            }
            R.id.menu_delete -> {
                confirmDeleteDomain(domain)
                true
            }
            else -> false
        }
    }
}