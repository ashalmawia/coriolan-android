package com.ashalmawia.coriolan.learning.exercise.sr

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.logbook.Logbook
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.Task
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.Status
import com.ashalmawia.coriolan.learning.exercise.Exercise
import com.ashalmawia.coriolan.learning.exercise.ExerciseExecutor
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.ExerciseListener
import com.ashalmawia.coriolan.learning.mutation.*
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.CardType
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Term
import com.ashalmawia.coriolan.util.forwardAndReverseWithState
import org.joda.time.DateTime

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

    override fun mutations(
            repository: Repository,
            preferences: Preferences,
            logbook: Logbook,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): List<Mutation> {
        return listOf(
                LearningModeMutation(repository),
                CardTypeMutation(cardType),
                SortReviewsByIntervalMutation,
                NewCardsOrderMutation.from(order),
                LimitCountMutation(preferences, logbook, date),
                ShuffleMutation(order == StudyOrder.RANDOM)
        )
    }

    override fun onTranslationAdded(repository: Repository, card: Card) {
        // TODO: decouple
        repository.updateCardLearningProgress(card, LearningProgress(emptyMap()))
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

    override fun pendingCards(repository: Repository, deck: Deck, date: DateTime): List<Task> {
        return repository.pendingCards(deck, date).map {
            Task(it.first, it.second, this)
        }
    }

    private fun createScheduler() = MultiplierBasedScheduler()

    class LearningModeMutation(private val repository: Repository) : Mutation {

        override fun apply(tasks: List<Task>): List<Task> {
            val (forward, reverse) = tasks.forwardAndReverseWithState()
            return forward.plus(reverse.filterReady())
        }

        private fun List<Task>.filterReady() : List<Task> {
            val translationIds = flatMap { it.card.translations }.map { it.id }

            val states = repository.getStatesForCardsWithOriginals(translationIds)

            return filter {
                it.status() != Status.NEW || it.card.translations.all { exp -> exp.isReady(states) }
            }
        }

        private fun Term.isReady(states: Map<Long, LearningProgress>): Boolean {
            val state = states[id]
            return state != null && state.flashcards.interval >= 4
        }
    }
}

private fun Task.status() = learningProgress.flashcards.status