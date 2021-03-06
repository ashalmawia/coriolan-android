package com.ashalmawia.coriolan.dependencies

import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.preference.PreferenceDataStore
import com.ashalmawia.coriolan.FirstStart
import com.ashalmawia.coriolan.FirstStartImpl
import com.ashalmawia.coriolan.data.DomainsRegistry
import com.ashalmawia.coriolan.data.DomainsRegistryImpl
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.importer.ImporterRegistry
import com.ashalmawia.coriolan.data.importer.ImporterRegistryImpl
import com.ashalmawia.coriolan.data.journal.Journal
import com.ashalmawia.coriolan.data.journal.sqlite.SqliteJournal
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.prefs.SharedPreferencesImpl
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteBackupHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteStorage
import com.ashalmawia.coriolan.learning.*
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactoryImpl
import com.ashalmawia.coriolan.learning.assignment.HistoryFactory
import com.ashalmawia.coriolan.learning.assignment.HistoryFactoryImpl
import com.ashalmawia.coriolan.learning.exercise.EmptyStateProvider
import com.ashalmawia.coriolan.learning.exercise.EmptyStateProviderImpl
import com.ashalmawia.coriolan.learning.exercise.sr.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.exercise.sr.Scheduler
import com.ashalmawia.coriolan.ui.settings.CoriolanPreferencesDataStore
import org.koin.dsl.module

val mainModule = module {
    single<TodayProvider> { TodayManager }
    single<Repository> { SqliteStorage(get(), get()) }
    single<Preferences> { SharedPreferencesImpl(get()) }
    single<Journal> { SqliteJournal(get()) }
    single<BackupableRepository> { SqliteBackupHelper(get(), get()) }
    single<PreferenceDataStore> { CoriolanPreferencesDataStore(get()) }
    single<ImporterRegistry> { ImporterRegistryImpl() }
    single<DomainsRegistry> { DomainsRegistryImpl(get()) }
    single<ExercisesRegistry> { ExercisesRegistryImpl(get(), get(), get(), get()) }
    single<AssignmentFactory> { AssignmentFactoryImpl(get(), get(), get(), get(), get()) }
    single<DeckCountsProvider> { DeckCountsProviderImpl(get()) }
    single<SQLiteOpenHelper> { SqliteRepositoryOpenHelper(get(), get()) }
    single<Scheduler> { MultiplierBasedScheduler(get()) }
    single<HistoryFactory> { HistoryFactoryImpl }
    single<EmptyStateProvider> { EmptyStateProviderImpl(get()) }

    factory<FirstStart> { FirstStartImpl(get()) }
}