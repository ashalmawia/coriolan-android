package com.ashalmawia.coriolan.ui.domains_list

import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.data.storage.Repository

class DomainsListViewModel(
        private val repository: Repository
) : ViewModel() {

    var view: DomainsListView? = null

    fun init(view: DomainsListView) {
        this.view = view
    }

    fun loadData() {
        val domains = repository.allDomains()
        view?.showDomains(domains)
    }
}