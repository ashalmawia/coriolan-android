package com.ashalmawia.coriolan.ui.domains_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DomainsListViewModel(
        private val repository: Repository,
        private val domainsRegistry: DomainsRegistry
) : ViewModel() {

    var view: DomainsListView? = null

    fun init(view: DomainsListView) {
        this.view = view
    }

    fun loadData() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val domains = repository.allDomains()
                withContext(Dispatchers.Main) {
                    view?.showDomains(domains)
                }
            }
        }
    }

    fun deleteDomain(domain: Domain) {
        view?.showLoading()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                domainsRegistry.deleteDomain(domain)
                loadData()
            }
        }
    }
}