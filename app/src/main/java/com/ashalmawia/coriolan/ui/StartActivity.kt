package com.ashalmawia.coriolan.ui

import android.os.Bundle
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.domains_list.DomainsListActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // refetch data in the domains registry
        DomainsRegistry.preinitialize(Repository.get(this))

        val defaultDomain = DomainsRegistry.domainIfExists()
        if (defaultDomain != null) {
            openDomain(defaultDomain)
        } else {
            openDomainCreation()
        }
    }

    private fun openDomain(domain: Domain) {
        finish()

        startActivity(DomainsListActivity.intent(this))
        startActivity(DomainActivity.intent(this, domain))

        overridePendingTransition(0, 0)
    }

    private fun openDomainCreation() {
        val intent = CreateDomainActivity.intent(this, true)
        startActivity(intent)
    }
}