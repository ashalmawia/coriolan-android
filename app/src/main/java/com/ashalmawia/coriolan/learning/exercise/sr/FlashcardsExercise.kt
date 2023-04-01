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
class FlashcardsExercise(
        private val repository: Repository
) : Exercise {

    override val id: ExerciseId
        get() = ExerciseId.FLASHCARDS

    @StringRes
    override fun name(): Int {
        return R.string.exercise_flashcards
    }

    override val canUndo: Boolean
        get() = true

    private fun getStatesForCardsWithOriginals(originals: List<Long>): Map<Long, LearningProgress> {
        return repository.getStatesForCardsWithOriginals(originals)
    }

    override fun mutations(
            preferences: Preferences,
            logbook: Logbook,
            date: DateTime,
            order: StudyOrder,
            deck: Deck,
            cardType: CardType
    ): List<Mutation> {
        return listOf(
                LearningModeMutation(this),
                CardTypeMutation(cardType),
                SortReviewsByPeriodMutation,
                NewCardsOrderMutation.from(order),
                LimitCountMutation(preferences, logbook, date),
                ShuffleMutation(order == StudyOrder.RANDOM)
        )
    }

    override fun onTranslationAdded(card: Card) {
        // TODO: decouple
        repository.updateCardLearningProgress(card, LearningProgress(emptyMap()))
    }

    override fun createExecutor(
            context: Context,
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

    override fun pendingCards(deck: Deck, date: DateTime): List<Task> {
        return repository.pendingCards(deck, date).map {
            Task(it.first, it.second, this)
        }
    }

    override fun status(learningProgress: LearningProgress): Status = learningProgress.spacedRepetition.status

    private fun createScheduler() = MultiplierBasedScheduler()

    class LearningModeMutation(private val exercise: FlashcardsExercise) : Mutation {

        override fun apply(tasks: List<Task>): List<Task> {
            val (forward, reverse) = tasks.forwardAndReverseWithState()
            return forward.plus(reverse.filterReady())
        }

        private fun List<Task>.filterReady() : List<Task> {
            val translationIds = flatMap { it.card.translations }.map { it.id }

            val states = exercise.getStatesForCardsWithOriginals(translationIds)

            return filter {
                it.status() != Status.NEW || it.card.translations.all { exp -> exp.isReady(states) }
            }
        }

        private fun Term.isReady(states: Map<Long, LearningProgress>): Boolean {
            val state = states[id]
            return state != null && state.spacedRepetition.period >= 4
        }
    }
}

private fun Task.status() = learningProgress.spacedRepetition.status