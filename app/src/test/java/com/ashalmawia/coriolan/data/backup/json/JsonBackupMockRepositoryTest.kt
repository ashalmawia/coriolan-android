package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JsonBackupMockRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(exercises: List<ExerciseDescriptor<*, *>>): BackupableRepository
            = MockBackupableRepository.empty(exercises)

    override fun createNonEmptyRepo(exercises: List<ExerciseDescriptor<*, *>>): BackupableRepository
            = MockBackupableRepository.random(exercises)
}