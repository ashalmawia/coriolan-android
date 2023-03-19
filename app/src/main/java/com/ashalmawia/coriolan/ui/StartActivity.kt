package com.ashalmawia.coriolan.ui

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.domains_list.DomainsListActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import org.koin.android.ext.android.get

class StartActivity : BaseActivity() {

    private val repository: Repository = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val domains = repository.allDomains()

        when (domains.size) {
            0 -> openDomainCreation()
            1 -> openDomain(domains[0])
            else -> openDomainsList()
        }
    }

    private fun openDomainsList() {
        startActivity(DomainsListActivity.intent(this))
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