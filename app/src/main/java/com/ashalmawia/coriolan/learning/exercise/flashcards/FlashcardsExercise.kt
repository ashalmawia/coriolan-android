package com.ashalmawia.coriolan.learning.exercise.flashcards

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.CardWithProgress
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.model.Card

/**
 * Simple learning exercise which shows all the cards in the assignment: given front, guess back.
 * After seeing the back side, user asserts themselves.
 *
 * If the card is answered correctly, it removes it from the queue.
 * Otherwise, adds it to the end of the queue.
 */
class FlashcardsExercise : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.FLASHCARDS

    @StringRes
    override fun name(): Int {
        return R.string.exercise_flashcards
    }

    override val canUndo: Boolean
        get() = true

    override fun onTranslationAdded(repository: Repository, card: Card) {
        // TODO: decouple
        repository.updateCardLearningProgress(card, LearningProgress.empty())
    }

    override fun createExecutor(
            context: Context,
            repository: Repository,
            uiContainer: ViewGroup,
            logbook: Logbook,
            listener: ExerciseListener
    ): ExerciseExecutor {
        return FlashcardsExerciseExecutor(
                context,
                this,
                repository,
                logbook,
                createScheduler(),
                uiContainer,
                listener
        )
    }

    override fun generateTasks(cards: List<CardWithProgress>): List<Task> {
        return cards.map { Task(it.card, it.learningProgress, this) }
    }

    private fun createScheduler() = MultiplierBasedScheduler()

}