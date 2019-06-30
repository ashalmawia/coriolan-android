package com.ashalmawia.coriolan.ui

import android.os.Bundle
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import org.koin.android.ext.android.get

class StartActivity : BaseActivity() {

    private val repository: Repository = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val domains = repository.allDomains()

        when (domains.size) {
            0 -> openDomainCreation()
            1 -> openDomain(domains[0])
            else -> openDomainsList()
        }
    }

    private fun openDomainsList() {
        navigator.openDomainsList()
    }

    private fun openDomain(domain: Domain) {
        navigator.openDomainWithStack(domain)
    }

    private fun openDomainCreation() {
        navigator.openCreateDomainScreen(true)
    }
}