package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.learning.Exercise
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JsonBackupMockRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(exercises: List<Exercise<*, *>>): BackupableRepository
            = MockBackupableRepository.empty(exercises)

    override fun createNonEmptyRepo(exercises: List<Exercise<*, *>>): BackupableRepository
            = MockBackupableRepository.random(exercises)
}