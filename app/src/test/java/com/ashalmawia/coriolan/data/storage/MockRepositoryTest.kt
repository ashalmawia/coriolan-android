package com.ashalmawia.coriolan.data.storage

import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class MockRepositoryTest : StorageTest() {

    override fun createStorage(exercises: List<ExerciseDescriptor<*, *>>): Repository {
        return MockRepository()
    }
}