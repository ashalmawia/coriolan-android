package com.ashalmawia.coriolan.ui.main

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DomainViewModel(
        private val repository: Repository,
        private val preferences: Preferences,
        private val domainsRegistry: DomainsRegistry,
        private val domainId: DomainId
) : ViewModel() {

    lateinit var domain: Domain

    var view: DomainView? = null

    fun init(view: DomainView) {
        this.view = view
        refresh()
    }

    fun refresh() {
        domain = repository.domainById(domainId)!!
        view?.bind(domain)
    }

    fun onDecksListFragmentInflated(firstDeckView: View) {
        if (!preferences.isMainFeatureDiscoverySeen()) {
            view?.showMainFeatureDiscovery(firstDeckView)
        }
    }

    fun onMainFeatureDiscoveryDismissed() {
        preferences.recordMainFeatureDiscoverySeen()
    }

    fun deleteDomain() {
        view?.showLoading()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                domainsRegistry.deleteDomain(domain)
            }
            withContext(Dispatchers.Main) {
                view?.finish()
            }
        }
    }
}