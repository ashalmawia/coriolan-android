package com.ashalmawia.coriolan.data.storage

import android.content.Context
import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbookOpenHelper
import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import org.robolectric.RuntimeEnvironment

private var count = 0
private fun name() = "test_${count++}.db"

fun provideContext(): Context = RuntimeEnvironment.getApplication()

fun provideRepositoryHelper() = SqliteRepositoryOpenHelper(provideContext(), name())
fun provideLogbookHelper() = SqliteLogbookOpenHelper(provideContext(), name())