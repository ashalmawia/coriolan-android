package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import android.support.v7.preference.PreferenceDataStore
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.DomainsRegistryImpl
import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.importer.*
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactoryImpl
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.DataFetcher
import com.ashalmawia.coriolan.ui.DecksAdapter
import com.ashalmawia.coriolan.ui.settings.CardTypePreferenceHelper
import com.ashalmawia.coriolan.ui.settings.CardTypePreferenceHelperImpl
import com.ashalmawia.coriolan.ui.settings.CoriolanPreferencesDataStore
import org.koin.android.ext.android.getKoin
import org.koin.core.Koin
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val SCOPE_DOMAIN = "scope_domain"
private const val SCOPE_DATA_IMPORT = "scope_data_import"

private const val DOMAIN_PROPERTY = "domain"

val mainModule = module {
    single { SqliteStorage(get(), get()) }
    single { Preferences.get(get()) }
    single { Journal.get(get()) }
    single<BackupableRepository> { SqliteBackupHelper(get(), get()) }
    single { Backup.get() }
    single<CardTypePreferenceHelper> { CardTypePreferenceHelperImpl() }
    single<PreferenceDataStore> { CoriolanPreferencesDataStore(get(), get()) }
    single<ImporterRegistry> { ImporterRegistryImpl() }
    single<DomainsRegistry> { DomainsRegistryImpl(get()) }
    single<ExercisesRegistry> { ExercisesRegistryImpl(get()) }
    single<AssignmentFactory> { AssignmentFactoryImpl(get(), get(), get()) }
    single<DeckCountsProvider> { DeckCountsProviderImpl(get()) }

    factory { (exercise: Exercise<*, *>, dataFetcher: DataFetcher) ->
        DecksAdapter(get(), get(), get(), get(), get(), get(), exercise, dataFetcher)
    }

    scope(named(SCOPE_DOMAIN)) {
        scoped { getProperty<Domain>(DOMAIN_PROPERTY) }
        scoped { DecksRegistry(get(), get(), get(), get()) }
    }

    scope(named(SCOPE_DATA_IMPORT)) {
        scoped<DataImportFlow> { (importer: DataImporter) -> DataImportFlowImpl(get(), get(), importer) }
    }
}

fun ComponentCallbacks.domainScope() = getKoin().getScope(SCOPE_DOMAIN)

fun ComponentCallbacks.dataImportScope() = getKoin().getScope(SCOPE_DATA_IMPORT)

fun Koin.recreateDomainScope(domain: Domain) {
    val scope = getScope(SCOPE_DOMAIN)
    scope.close()
    setProperty(DOMAIN_PROPERTY, domain)
    createScope(SCOPE_DOMAIN, named(SCOPE_DOMAIN))
}