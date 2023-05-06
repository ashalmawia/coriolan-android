package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.model.mockCardWithProgress
import com.ashalmawia.coriolan.model.mockLearningProgressInProgress
import com.ashalmawia.coriolan.model.mockLearningProgressNew
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StudyOrderMutationTest {
    
    private fun cards(new: Boolean) = List(50) { i ->
        mockCardWithProgress(
                if (new) mockLearningProgressNew() else mockLearningProgressInProgress(),
                id = i.toLong())
    }

    @Test
    fun test__orderAdded__new() {
        // given
        val cards = cards(true)
        val mutation = OrderAdded()
        
        // when
        val processed = mutation.apply(cards)
        
        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__random__new() {
        // given
        val cards = cards(true)
        val mutation = Random()

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards, processed.sortedBy { it.card.id })
    }

    @Test
    fun test__newestFirst__new() {
        // given
        val cards = cards(true)
        val mutation = NewestFirst()

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.reversed(), processed)
    }
}