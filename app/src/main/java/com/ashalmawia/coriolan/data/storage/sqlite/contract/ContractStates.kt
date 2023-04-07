package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.data.storage.sqlite.put
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.sr.ExerciseState
import com.ashalmawia.coriolan.data.storage.sqlite.date
import com.ashalmawia.coriolan.data.storage.sqlite.int
import com.ashalmawia.coriolan.data.storage.sqlite.isNull
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.string
import org.joda.time.DateTime

object ContractStates {

    const val STATES = "States"

    const val STATES_CARD_ID = "States_CardId"
    const val STATES_EXERCISE = "States_Exercise"
    const val STATES_DUE_DATE = "States_DueDate"
    const val STATES_PERIOD = "States_Period"

    private val allColumns = arrayOf(
            STATES_CARD_ID,
            STATES_EXERCISE,
            STATES_DUE_DATE,
            STATES_PERIOD
    )
    fun allColumnsStates(alias: String? = null): String = SqliteUtils.allColumns(allColumns, alias)


    fun Cursor.statesCardId(): Long { return long(STATES_CARD_ID) }

    fun Cursor.statesExerciseId(): ExerciseId {
        val value = string(STATES_EXERCISE)
        return ExerciseId.fromValue(value)
    }
    fun Cursor.statesDateDue(): DateTime { return date(STATES_DUE_DATE) }
    fun Cursor.statesPeriod(): Int { return int(STATES_PERIOD) }
    fun Cursor.statesHasSavedExerciseState(): Boolean { return !isNull(STATES_EXERCISE) }
    fun Cursor.exerciseState(): ExerciseState {
        return ExerciseState(statesDateDue(), statesPeriod())
    }

    fun createAllLearningProgressContentValues(
            cardId: Long, learningProgress: LearningProgress): List<ContentValues> {
        return learningProgress.states.map {
            (exerciseId, _) -> createCardStateContentValues(cardId, exerciseId, learningProgress)
        }
    }

    fun createCardStateContentValues(cardId: Long, exerciseId: ExerciseId, learningProgress: LearningProgress): ContentValues {
        val state = learningProgress.stateFor(exerciseId)
        return createCardStateContentValues(cardId, exerciseId, state.due, state.period)
    }

    fun createCardStateContentValues(cardId: Long, exerciseId: ExerciseId, due: DateTime, period: Int): ContentValues {
        val cv = ContentValues()
        cv.put(STATES_CARD_ID, cardId)
        cv.put(STATES_EXERCISE, exerciseId.value)
        cv.put(STATES_DUE_DATE, due)
        cv.put(STATES_PERIOD, period)
        return cv
    }
}