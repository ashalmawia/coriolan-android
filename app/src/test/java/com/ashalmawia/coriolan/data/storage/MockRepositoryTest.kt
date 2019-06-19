package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.Exercise
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MockRepositoryTest : StorageTest() {

    override fun createStorage(exercises: List<Exercise<*, *>>): Repository {
        return MockRepository()
    }
}