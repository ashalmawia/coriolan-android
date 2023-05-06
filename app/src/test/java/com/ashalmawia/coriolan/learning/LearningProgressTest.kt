package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.model.mockLearningProgressInProgress
import com.ashalmawia.coriolan.model.mockLearningProgressLearnt
import com.ashalmawia.coriolan.model.mockLearningProgressNew
import com.ashalmawia.coriolan.model.mockLearningProgressRelearn
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LearningProgressTest {

    @Test
    fun `empty map - global status - new`() {
        // when
        val lp = LearningProgress.empty()

        // then
        assertEquals(Status.NEW, lp.status)
    }

    @Test
    fun `single exercise - global status - new`() {
        // when
        val lp = mockLearningProgressNew()

        // then
        assertEquals(Status.NEW, lp.status)
    }

    @Test
    fun `single exercise - global status - in progress`() {
        // when
        val lp = mockLearningProgressInProgress()

        // then
        assertEquals(Status.IN_PROGRESS, lp.status)
    }

    @Test
    fun `single exercise - global status - relearn`() {
        // when
        val lp = mockLearningProgressRelearn()

        // then
        assertEquals(Status.RELEARN, lp.status)
    }

    @Test
    fun `single exercise - global status - learnt`() {
        // when
        val lp = mockLearningProgressLearnt()

        // then
        assertEquals(Status.LEARNT, lp.status)
    }
}