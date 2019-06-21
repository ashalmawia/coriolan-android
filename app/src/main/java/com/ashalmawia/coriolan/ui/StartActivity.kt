package com.ashalmawia.coriolan.ui

import android.os.Bundle
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.domains_list.DomainsListActivity
import org.koin.android.ext.android.get

class StartActivity : BaseActivity() {

    private val domainsRegistry: DomainsRegistry = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultDomain = domainsRegistry.defaultDomain()
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