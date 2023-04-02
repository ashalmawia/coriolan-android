package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.model.mockLearningProgressInProgress
import com.ashalmawia.coriolan.model.mockLearningProgressLearnt
import com.ashalmawia.coriolan.model.mockLearningProgressNew
import com.ashalmawia.coriolan.model.mockLearningProgressRelearn
import com.ashalmawia.coriolan.model.mockStateInProgress
import com.ashalmawia.coriolan.model.mockStateLearnt
import com.ashalmawia.coriolan.model.mockStateNew
import com.ashalmawia.coriolan.model.mockStateRelearn
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LearningProgressTest {

    @Test
    fun `empty map - global status - new`() {
        // when
        val lp = LearningProgress(emptyMap())

        // then
        assertEquals(Status.NEW, lp.globalStatus)
    }

    @Test
    fun `single exercise - global status - new`() {
        // when
        val lp = mockLearningProgressNew()

        // then
        assertEquals(Status.NEW, lp.globalStatus)
    }

    @Test
    fun `single exercise - global status - in progress`() {
        // when
        val lp = mockLearningProgressInProgress()

        // then
        assertEquals(Status.IN_PROGRESS, lp.globalStatus)
    }

    @Test
    fun `single exercise - global status - relearn`() {
        // when
        val lp = mockLearningProgressRelearn()

        // then
        assertEquals(Status.RELEARN, lp.globalStatus)
    }

    @Test
    fun `single exercise - global status - learnt`() {
        // when
        val lp = mockLearningProgressLearnt()

        // then
        assertEquals(Status.LEARNT, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - both new - new`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateNew(),
                ExerciseId.TEST to mockStateNew()
        ))

        // then
        assertEquals(Status.NEW, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - both relearn - relearn`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateRelearn(),
                ExerciseId.TEST to mockStateRelearn()
        ))

        // then
        assertEquals(Status.RELEARN, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - both in progress - in progress`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateInProgress(),
                ExerciseId.TEST to mockStateInProgress()
        ))

        // then
        assertEquals(Status.IN_PROGRESS, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - both learnt - learnt`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateLearnt(),
                ExerciseId.TEST to mockStateLearnt()
        ))

        // then
        assertEquals(Status.LEARNT, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - new & in progress - in progress`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateNew(),
                ExerciseId.TEST to mockStateInProgress()
        ))

        // then
        assertEquals(Status.IN_PROGRESS, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - new & learnt - in progress`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateNew(),
                ExerciseId.TEST to mockStateLearnt()
        ))

        // then
        assertEquals(Status.IN_PROGRESS, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - both new & relearn - relearn`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateNew(),
                ExerciseId.TEST to mockStateRelearn()
        ))

        // then
        assertEquals(Status.RELEARN, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - in progress & learnt - in progress`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateInProgress(),
                ExerciseId.TEST to mockStateLearnt()
        ))

        // then
        assertEquals(Status.IN_PROGRESS, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - in progress & relearn - relearn`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateInProgress(),
                ExerciseId.TEST to mockStateRelearn()
        ))

        // then
        assertEquals(Status.RELEARN, lp.globalStatus)
    }

    @Test
    fun `multiple exercises - global status - relearn & learnt - relearn`() {
        // when
        val lp = LearningProgress(mapOf(
                ExerciseId.FLASHCARDS to mockStateRelearn(),
                ExerciseId.TEST to mockStateLearnt()
        ))

        // then
        assertEquals(Status.RELEARN, lp.globalStatus)
    }
}