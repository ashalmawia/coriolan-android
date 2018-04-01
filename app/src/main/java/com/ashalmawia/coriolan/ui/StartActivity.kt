package com.ashalmawia.coriolan.ui

import android.os.Bundle
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // refetch data in the domains registry
        DomainsRegistry.preinitialize(Repository.get(this))

        val defaultDomain = DomainsRegistry.domain()
        if (defaultDomain != null) {
            openDomain(defaultDomain)
        } else {
            openDomainCreation()
        }
    }

    private fun openDomain(domain: Domain) {
        val intent = DomainActivity.intent(this, domain)
        startActivity(intent)
        finish()
    }

    private fun openDomainCreation() {
        val intent = CreateDomainActivity.intent(this, true)
        startActivity(intent)
    }
}