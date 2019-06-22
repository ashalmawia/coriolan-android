package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.model.Domain
import org.koin.android.ext.android.getKoin
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val SCOPE_DOMAIN = "scope_domain"

private const val PROPERTY_DOMAIN = "domain"

val domainModule = module {

    scope(named(SCOPE_DOMAIN)) {
        scoped { getProperty<Domain>(PROPERTY_DOMAIN) }
        scoped { DecksRegistry(get(), get(), get(), get()) }
    }
}

fun Scope.domainScope() = getScope(SCOPE_DOMAIN)
fun ComponentCallbacks.domainScope() = getKoin().getScope(SCOPE_DOMAIN)

fun Koin.recreateDomainScope(domain: Domain) {
    val scope = getScopeOrNull(SCOPE_DOMAIN)
    scope?.close()
    setProperty(PROPERTY_DOMAIN, domain)
    createScope(SCOPE_DOMAIN, named(SCOPE_DOMAIN))
}