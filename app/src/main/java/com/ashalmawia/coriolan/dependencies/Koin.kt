package com.ashalmawia.coriolan.dependencies

import android.content.ComponentCallbacks
import android.database.sqlite.SQLiteOpenHelper
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
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactoryImpl
import com.ashalmawia.coriolan.learning.assignment.HistoryFactory
import com.ashalmawia.coriolan.learning.assignment.HistoryFactoryImpl
import com.ashalmawia.coriolan.learning.exercise.sr.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.exercise.sr.Scheduler
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BeginStudyListener
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
private const val SCOPE_LEARNING_FLOW = "scope_learning_flow"
private const val SCOPE_DATA_IMPORT = "scope_data_import"

private const val PROPERTY_DOMAIN = "domain"

val mainModule = module {
    single { SqliteStorage(get()) }
    single { Preferences.get(get()) }
    single { Journal.get(get()) }
    single<BackupableRepository> { SqliteBackupHelper(get(), get()) }
    single { Backup.get() }
    single<CardTypePreferenceHelper> { CardTypePreferenceHelperImpl() }
    single<PreferenceDataStore> { CoriolanPreferencesDataStore(get(), get()) }
    single<ImporterRegistry> { ImporterRegistryImpl() }
    single<DomainsRegistry> { DomainsRegistryImpl(get()) }
    single<ExercisesRegistry> { ExercisesRegistryImpl(get(), get()) }
    single<AssignmentFactory> { AssignmentFactoryImpl(get(), get(), get(), get()) }
    single<DeckCountsProvider> { DeckCountsProviderImpl(get()) }
    single<SQLiteOpenHelper> { SqliteRepositoryOpenHelper(get(), get()) }
    single<Scheduler> { MultiplierBasedScheduler() }
    single { HistoryFactoryImpl }

    factory { (exercise: Exercise<*, *>, dataFetcher: DataFetcher, beginStudyListener: BeginStudyListener) ->
        DecksAdapter(get(), get(), get(), get(), exercise, dataFetcher, beginStudyListener)
    }

    scope(named(SCOPE_DOMAIN)) {
        scoped { getProperty<Domain>(PROPERTY_DOMAIN) }
        scoped { DecksRegistry(get(), get(), get(), get()) }
    }

    scope(named(SCOPE_LEARNING_FLOW)) {
        scoped { (exercise: Exercise<*, *>, deck: Deck, studyOrder: StudyOrder) ->
            LearningFlow(get(), get(), deck, studyOrder, exercise, get())
        }
    }

    scope(named(SCOPE_DATA_IMPORT)) {
        scoped<DataImportFlow> { (importer: DataImporter) -> DataImportFlowImpl(get(), get(), importer) }
    }
}

fun ComponentCallbacks.domainScope() = getKoin().getScope(SCOPE_DOMAIN)

fun ComponentCallbacks.learningFlowScope() = getKoin().getScope(SCOPE_LEARNING_FLOW)

fun ComponentCallbacks.dataImportScope() = getKoin().getScope(SCOPE_DATA_IMPORT)

fun Koin.recreateDomainScope(domain: Domain) {
    val scope = getScope(SCOPE_DOMAIN)
    scope.close()
    setProperty(PROPERTY_DOMAIN, domain)
    createScope(SCOPE_DOMAIN, named(SCOPE_DOMAIN))
}