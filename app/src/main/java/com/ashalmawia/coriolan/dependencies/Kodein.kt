package com.ashalmawia.coriolan.dependencies

import android.support.v7.preference.PreferenceDataStore
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.settings.CoriolanPreferencesDataStore
import org.kodein.di.Kodein
import org.kodein.di.bindings.Scope
import org.kodein.di.bindings.WeakContextScope
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.scoped
import org.kodein.di.generic.singleton

val mainModule = Kodein.Module("main", false) {
    bind<Repository>() with singleton { Repository.get(instance()) }
    bind<Preferences>() with singleton { Preferences.get(instance()) }
    bind<Journal>() with singleton { Journal.get(instance()) }
    bind<BackupableRepository>() with singleton { BackupableRepository.get(instance()) }
    bind<Backup>() with singleton { Backup.get() }
    bind<PreferenceDataStore>() with singleton { CoriolanPreferencesDataStore(instance()) }
}

fun domainModule(domain: Domain) = Kodein.Module("domain", false) {

    bind<Domain>() with scoped(DomainScope).singleton { domain }
    bind<DecksRegistry>() with scoped(DomainScope).singleton { DecksRegistry.get(instance(), instance()) }
}

object DomainScope : WeakContextScope<Domain>()