package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import org.robolectric.RuntimeEnvironment

private var count = 0
private fun name() = "test_${count++}.db"

fun provideHelper() = SqliteRepositoryOpenHelper(RuntimeEnvironment.application, name())