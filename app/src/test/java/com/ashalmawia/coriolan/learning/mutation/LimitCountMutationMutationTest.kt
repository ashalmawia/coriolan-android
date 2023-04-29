package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.model.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LimitCountMutationMutationTest {

    private val cards = List(60) { i -> mockTask(procudeMockLearningProgress(i)) }

    private fun procudeMockLearningProgress(i: Int) =
            when (i % 4) {
                0 -> mockLearningProgressNew()
                1 -> mockLearningProgressInProgress()
                2 -> mockLearningProgressRelearn()
                else -> mockLearningProgressLearnt()
            }
    
    private fun mutationFromTargets(new: Int?, review: Int?) = LimitCountMutation(StudyTargets(new, review))

    @Test
    fun test__noLimits() {
        // given
        val mutation = mutationFromTargets(null, null)
        
        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__noNew() {
        // given
        val mutation = mutationFromTargets(0, null)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.size - cards.count(Status.NEW), processed.size)
        assertEquals(0, processed.count(Status.NEW))
    }

    @Test
    fun test__noReview() {
        // given
        val mutation = mutationFromTargets(null, 0)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.count(Status.NEW, Status.RELEARN), processed.size)
        assertEquals(cards.filter(Status.NEW, Status.RELEARN), processed)
    }

    @Test
    fun test__allZero() {
        // given
        val mutation = mutationFromTargets(0, 0)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN), processed.size)
    }

    @Test
    fun test__limitOnlyNew() {
        // given
        val mutation = mutationFromTargets(3, null)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.count(Status.RELEARN, Status.IN_PROGRESS, Status.LEARNT) + 3, processed.size)
        assertEquals(3, processed.count(Status.NEW))
        assertEquals(cards.count(Status.RELEARN, Status.IN_PROGRESS, Status.LEARNT),
                processed.count(Status.RELEARN, Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__limitOnlyReview() {
        // given
        val mutation = mutationFromTargets(null, 5)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards.count(Status.NEW, Status.RELEARN) + 5, processed.size)
        assertEquals(5, processed.count(Status.IN_PROGRESS, Status.LEARNT))
        assertEquals(cards.count(Status.NEW, Status.RELEARN),
                processed.count(Status.NEW, Status.RELEARN))
    }

    @Test
    fun test__limitsTooHigh() {
        // given
        val mutation = mutationFromTargets(cards.size * 2, cards.size * 2)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__limitsEqual() {
        // given
        val mutation = mutationFromTargets(15, 15)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(30 + cards.count(Status.RELEARN), processed.size)
        assertEquals(15, processed.count(Status.NEW))
        assertEquals(15, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }

    @Test
    fun test__realLimits__less() {
        // given
        val mutation = mutationFromTargets(5, 12)

        // when
        val processed = mutation.apply(cards)

        // then
        assertEquals(5 + 12 + cards.count(Status.RELEARN), processed.size)
        assertEquals(5, processed.count(Status.NEW))
        assertEquals(12, processed.count(Status.IN_PROGRESS, Status.LEARNT))
    }
}

private fun List<Task>.filter(vararg statuses: Status): List<Task> {
    return filter { statuses.contains(it.learningProgress.globalStatus) }
}

private fun List<Task>.count(vararg statuses: Status): Int {
    return count { statuses.contains(it.learningProgress.globalStatus) }
}