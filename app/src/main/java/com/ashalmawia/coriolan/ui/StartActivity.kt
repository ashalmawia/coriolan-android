package com.ashalmawia.coriolan.ui

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.domain_add_edit.AddEditDomainActivity
import com.ashalmawia.coriolan.ui.domains_list.DomainsListActivity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import com.ashalmawia.coriolan.ui.onboarding.OnboardingActivity
import org.koin.android.ext.android.inject

class StartActivity : BaseActivity() {

    private val repository: Repository by inject()
    private val preferences: Preferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (!preferences.isOnboardingCompleted()) {
            navigateToOnboarding()
        } else {
            navigateToDomains()
        }
    }

    private fun navigateToOnboarding() {
        val intent = OnboardingActivity.intent(this)
        startActivity(intent)
    }

    private fun navigateToDomains() {
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
        val intent = AddEditDomainActivity.intent(this, true)
        startActivity(intent)
    }
}