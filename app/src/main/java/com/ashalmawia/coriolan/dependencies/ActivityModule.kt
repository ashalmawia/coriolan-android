package com.ashalmawia.coriolan.dependencies

import android.app.Activity
import com.ashalmawia.coriolan.ui.main.DomainActivity
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val SCOPE_DOMAIN_ACITIVTY = "scope_domain_activity"

val activityModule = module {

    scope(named(SCOPE_DOMAIN_ACITIVTY)) {
        scoped<Activity> { (activity: DomainActivity) -> activity }
    }
}

fun Scope.domainActivityScope() = getKoin().getScope(SCOPE_DOMAIN_ACITIVTY)

fun DomainActivity.createScope() {
    val scope = getKoin().createScope(SCOPE_DOMAIN_ACITIVTY, named(SCOPE_DOMAIN_ACITIVTY))
    scope.get<Activity> { parametersOf(this) }
}

fun DomainActivity.closeScope() {
    getKoin().getScope(SCOPE_DOMAIN_ACITIVTY).close()
}