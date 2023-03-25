package com.ashalmawia.coriolan.learning.mutation

import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.util.new
import com.ashalmawia.coriolan.util.review

class ShuffleMutation(private val shuffle: Boolean) : Mutation {

    override fun apply(tasks: List<Task>): List<Task> {
        return if (shuffle) {
            shuffle(tasks)
        } else {
            tasks
        }
    }

    private fun shuffle(tasks: List<Task>): List<Task> {
        val reviewOnlySize = tasks.size / 3
        val newCardsAllowedSize = tasks.size - reviewOnlySize

        val new = tasks.new()
        val review = tasks.review()

        return if (new.size >= newCardsAllowedSize) {
            new.shuffled().plus(review.shuffled())
        } else {
            // otherwise, keep the last X cards review only, to make sure all new cards are seen in advance
            val extraReviewsCount = newCardsAllowedSize - new.size

            val newCardsAllowed = new.plus(review.subList(0, extraReviewsCount))
            val reviewsOnly = review.subList(extraReviewsCount, review.size)

            newCardsAllowed.shuffled().plus(reviewsOnly.shuffled())
        }
    }
}