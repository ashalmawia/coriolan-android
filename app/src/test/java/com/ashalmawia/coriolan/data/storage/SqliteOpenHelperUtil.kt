package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.data.storage.sqlite.SqliteRepositoryOpenHelper
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import org.robolectric.RuntimeEnvironment

private var count = 0
private fun name() = "test_${count++}.db"

fun provideHelper(exercises: List<ExerciseDescriptor<*, *>>)
        = SqliteRepositoryOpenHelper(RuntimeEnvironment.application, exercises, name())