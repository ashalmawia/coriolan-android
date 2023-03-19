package com.ashalmawia.coriolan.data.backup

import com.ashalmawia.coriolan.data.backup.json.MockBackupableRepository
import com.ashalmawia.coriolan.learning.exercise.ExercisesRegistry
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockBackupableRepositoryTest : BackupableRepositoryTest() {

    override fun createRepository(): BackupableRepository
            = MockBackupableRepository.empty()
}