package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.BackupableRepository
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JsonBackupMockRepositoryTest : JsonBackupTest() {

    override fun createEmptyRepo(): BackupableRepository
            = MockBackupableRepository.empty()

    override fun createNonEmptyRepo(): BackupableRepository
            = MockBackupableRepository.random()
}