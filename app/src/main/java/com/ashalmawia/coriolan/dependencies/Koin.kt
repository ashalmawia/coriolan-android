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
import com.ashalmawia.coriolan.learning.DeckCountsProvider
import com.ashalmawia.coriolan.learning.DeckCountsProviderImpl
import com.ashalmawia.coriolan.learning.ExercisesRegistry
import com.ashalmawia.coriolan.learning.ExercisesRegistryImpl
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactory
import com.ashalmawia.coriolan.learning.assignment.AssignmentFactoryImpl
import com.ashalmawia.coriolan.learning.assignment.HistoryFactory
import com.ashalmawia.coriolan.learning.assignment.HistoryFactoryImpl
import com.ashalmawia.coriolan.learning.exercise.sr.MultiplierBasedScheduler
import com.ashalmawia.coriolan.learning.exercise.sr.Scheduler
import com.ashalmawia.coriolan.ui.settings.CardTypePreferenceHelper
import com.ashalmawia.coriolan.ui.settings.CardTypePreferenceHelperImpl
import com.ashalmawia.coriolan.ui.settings.CoriolanPreferencesDataStore
import org.koin.dsl.module

val mainModule = module {
    single<Repository> { SqliteStorage(get()) }
    single<Preferences> { SharedPreferencesImpl(get()) }
    single<Journal> { SqliteJournal(get()) }
    single<BackupableRepository> { SqliteBackupHelper(get(), get()) }
    single<CardTypePreferenceHelper> { CardTypePreferenceHelperImpl() }
    single<PreferenceDataStore> { CoriolanPreferencesDataStore(get(), get()) }
    single<ImporterRegistry> { ImporterRegistryImpl() }
    single<DomainsRegistry> { DomainsRegistryImpl(get()) }
    single<ExercisesRegistry> { ExercisesRegistryImpl(get(), get()) }
    single<AssignmentFactory> { AssignmentFactoryImpl(get(), get(), get(), get()) }
    single<DeckCountsProvider> { DeckCountsProviderImpl(get()) }
    single<SQLiteOpenHelper> { SqliteRepositoryOpenHelper(get(), get()) }
    single<Scheduler> { MultiplierBasedScheduler() }
    single<HistoryFactory> { HistoryFactoryImpl }

    factory<FirstStart> { FirstStartImpl(get()) }
}