package com.ashalmawia.coriolan.ui.main

import android.view.View
import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId

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
        domainsRegistry.deleteDomain(domain)
        view?.finish()
    }
}