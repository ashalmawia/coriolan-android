package com.ashalmawia.coriolan.dependencies

import android.support.v7.preference.PreferenceDataStore
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.ui.settings.CoriolanPreferencesDataStore
import org.koin.dsl.module

val mainModule = module {
    single { Repository.get(get()) }
    single { Preferences.get(get()) }
    single { Journal.get(get()) }
    single { BackupableRepository.get(get()) }
    single { Backup.get() }
    single<PreferenceDataStore> { CoriolanPreferencesDataStore(get()) }
}

val domainModule = module {
    single { DomainsRegistry.domain() }
    single { DecksRegistry.get(get(), get()) }
}