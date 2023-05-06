package com.ashalmawia.coriolan.learning.assignment

import com.ashalmawia.coriolan.model.Counts
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.util.OpenForTesting
import org.joda.time.DateTime
import java.util.*
import kotlin.math.min

private const val RESCHEDULING_STEP = 20

@OpenForTesting
class Assignment(
        val date: DateTime,
        private val history: History,
        tasks: List<Task>
) {
    private val queue = LinkedList(tasks)

    val originalCount = tasks.size

    var current: Task? = null
        protected set

    fun counts(): Counts {
        val tasks = tasks()
        val counts = tasks.groupBy { it.learningProgress.status }.mapValues { it.value.size }
        return Counts.createFrom(counts)
    }
    fun hasNext(): Boolean {
        return queue.size > 0
    }

    fun reschedule(task: Task) {
        val index = min(RESCHEDULING_STEP, queue.size)
        queue.add(index, task)
    }

    fun delete(card: Card) {
        queue.removeAll { it.card == card }
        if (current?.card == card) {
            current = null
        }
        history.forget(card)
    }

    fun next(): Task {
        val current = this.current
        if (current != null) {
            history.record(current)
        }

        val next = getNext()
        this.current = next
        return next
    }

    private fun getNext(): Task {
        return queue.poll() ?: throw IllegalStateException("queue is empty")
    }

    fun replace(old: Card, new: Task) {
        if (current?.card?.id == old.id) {
            current = new
        } else {
            val found = queue.find { it.card == old }
            if (found != null) {
                queue.remove(found)
                queue.offer(new)
            }
        }
    }

    fun undo(): Task {
        if (!canUndo()) {
            throw IllegalStateException("can not undo")
        }

        queue.add(0, current!!)
        val previous = history.goBack()
        queue.removeAll { it.card == previous.card }        // remove reschedules
        current = previous
        return previous
    }

    fun canUndo() = history.canGoBack() && current?.exercise?.canUndo ?: false

    private fun tasks(): List<Task> {
        val cur = current
        return if (cur != null) {
            queue.plus(cur)
        } else {
            queue.toList()
        }
    }
}