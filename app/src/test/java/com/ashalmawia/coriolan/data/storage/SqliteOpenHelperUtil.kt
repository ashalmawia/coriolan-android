package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.learning.MockExercisesRegistry
import org.robolectric.RuntimeEnvironment

private var count = 0
private fun name() = "test_${count++}.db"

fun provideHelper(exercises: MockExercisesRegistry)
        = SqliteRepositoryOpenHelper(RuntimeEnvironment.application, exercises, name())