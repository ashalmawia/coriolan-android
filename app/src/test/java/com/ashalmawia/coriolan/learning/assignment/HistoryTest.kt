package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.mockLearningProgress
import com.ashalmawia.coriolan.model.mockTask
import com.ashalmawia.coriolan.model.mockLearningProgressRelearn
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HistoryTest {

    private val history = SimpleHistory()

    @Test(expected = Exception::class)
    fun test__empty() {
        // when
        val canGoBack = history.canGoBack()

        // then
        assertFalse(canGoBack)

        // when
        history.goBack()
    }

    @Test
    fun test__single() {
        // given
        val task = mockTask(mockLearningProgressRelearn())

        // when
        history.record(task)

        // then
        assertTrue(history.canGoBack())

        // when
        val read = history.goBack()

        // then
        assertEquals(task, read)
    }

    @Test
    fun test__multiple() {
        // given
        val list = (0 until 10).map {
            mockTask(mockLearningProgress(period = it))
        }

        // when
        list.forEach { history.record(it) }

        // then
        assertTrue(history.canGoBack())

        // when
        list.reversed().forEach {
            assertTrue(history.canGoBack())
            // then
            assertEquals(it, history.goBack())
        }

        // then
        assertFalse(history.canGoBack())
    }
}