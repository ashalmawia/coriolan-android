package com.ashalmawia.coriolan.dependencies

import com.ashalmawia.coriolan.ui.AppMenu
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.Navigator
import com.ashalmawia.coriolan.ui.NavigatorImpl
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val SCOPE_ACITIVTY = "scope_activity"

val activityModule = module {

    scope(named(SCOPE_ACITIVTY)) {
        scoped { (activity: BaseActivity) -> activity }
        scoped<Navigator> { NavigatorImpl(get(), get(), get()) }
        scoped { AppMenu(get()) }
    }
}

fun BaseActivity.createScope(): Scope {
    val scope = getKoin().createScope(SCOPE_ACITIVTY + this::class.java.name, named(SCOPE_ACITIVTY))
    scope.get<BaseActivity> { parametersOf(this) }
    return scope
}