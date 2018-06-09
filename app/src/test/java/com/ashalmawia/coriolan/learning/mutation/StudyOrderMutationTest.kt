package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.scheduler.sr.SRState
import com.ashalmawia.coriolan.model.mockCardWithState
import com.ashalmawia.coriolan.model.mockStateInProgress
import com.ashalmawia.coriolan.model.mockStateNew
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class StudyOrderMutationTest {
    
    private fun cards(new: Boolean) = List(50, { i -> mockCardWithState(
            if (new) mockStateNew() else mockStateInProgress(),
            id = i.toLong()) 
    })
    
    @Test
    fun test__orderAdded__new() {
        // given
        val cards = cards(true)
        val mutation = OrderAdded<SRState>()
        
        // when
        val processed = mutation.apply(cards)
        
        // then
        assertEquals(cards, processed)
    }

    @Test
    fun test__random__new() {
        // given
        val cards = cards(true)
        val mutation = Random<SRState>()

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
        val mutation = NewestFirst<SRState>()

        // when
        val processed = mutation.apply(cards)

        // then
        assertFalse(cards == processed)
        assertEquals(cards.reversed(), processed)
    }
}