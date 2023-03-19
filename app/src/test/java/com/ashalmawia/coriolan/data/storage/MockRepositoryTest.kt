package com.ashalmawia.coriolan.data.storage

import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockRepositoryTest : StorageTest() {

    override fun createStorage(): Repository {
        return MockRepository()
    }
}