package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.model.Domain
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val SCOPE_DOMAIN = "scope_domain"

val domainModule = module {

    scope(named(SCOPE_DOMAIN)) {
        scoped { (domain: Domain) -> domain }
        scoped { DecksRegistry(get(), get(), get(), get()) }
    }
}

fun Scope.domainScope() = getScope(SCOPE_DOMAIN)
fun ComponentCallbacks.domainScope() = getKoin().getScope(SCOPE_DOMAIN)

private fun ComponentCallbacks.domainScopeOrNull() = getKoin().getScopeOrNull(SCOPE_DOMAIN)

fun ComponentCallbacks.createDomainScope(domain: Domain) {
    closeDomainScope()
    val scope = getKoin().createScope(SCOPE_DOMAIN, named(SCOPE_DOMAIN))
    scope.get<Domain> { parametersOf(domain) }
}
fun ComponentCallbacks.closeDomainScope() = domainScopeOrNull()?.close()