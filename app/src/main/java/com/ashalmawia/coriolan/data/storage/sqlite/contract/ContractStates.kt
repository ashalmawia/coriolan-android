package com.ashalmawia.coriolan.data.storage.sqlite.contract

import android.content.ContentValues
import android.database.Cursor
import com.ashalmawia.coriolan.data.storage.sqlite.put
import com.ashalmawia.coriolan.learning.LearningProgress
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.exercise.flashcards.ExerciseState
import com.ashalmawia.coriolan.data.storage.sqlite.date
import com.ashalmawia.coriolan.data.storage.sqlite.int
import com.ashalmawia.coriolan.data.storage.sqlite.intOrNull
import com.ashalmawia.coriolan.data.storage.sqlite.isNull
import com.ashalmawia.coriolan.data.storage.sqlite.long
import com.ashalmawia.coriolan.data.storage.sqlite.string
import com.ashalmawia.coriolan.learning.exercise.flashcards.INTERVAL_NEVER_SCHEDULED
import org.joda.time.DateTime

object ContractStates {

    const val STATES = "States"

    const val STATES_CARD_ID = "States_CardId"
    const val STATES_EXERCISE = "States_Exercise"
    const val STATES_DUE_DATE = "States_DueDate"
    const val STATES_INTERVAL = "States_Interval"
    const val STATES_IS_ACTIVE = "States_Active"
    const val STATES_PAYLOAD = "States_Payload"

    const val STATES__CARD_ACTIVE = 0
    //const val STATES__CARD_POSTPONED = 1
    //etc.

    private val allColumns = arrayOf(
            STATES_CARD_ID,
            STATES_EXERCISE,
            STATES_DUE_DATE,
            STATES_INTERVAL,
            STATES_IS_ACTIVE,
            STATES_PAYLOAD
    )
    fun allColumnsStates(alias: String? = null): String = SqliteUtils.allColumns(allColumns, alias)

    val createQuery = """
        CREATE TABLE $STATES(
            $STATES_CARD_ID INTEGER,
            $STATES_EXERCISE TEXT NOT NULL,
            $STATES_DUE_DATE INTEGER NOT NULL,
            $STATES_INTERVAL INTEGER NOT NULL,
            $STATES_IS_ACTIVE INTEGER NOT NULL,
            $STATES_PAYLOAD TEXT,
            
            PRIMARY KEY($STATES_CARD_ID, $STATES_EXERCISE),
            FOREIGN KEY ($STATES_CARD_ID) REFERENCES ${ContractCards.CARDS} (${ContractCards.CARDS_ID})
               ON DELETE CASCADE
               ON UPDATE CASCADE
        );
        
        CREATE INDEX idx_$STATES_DUE_DATE
        ON $STATES ($STATES_DUE_DATE);
        
        CREATE INDEX idx_$STATES_IS_ACTIVE
        ON $STATES ($STATES_IS_ACTIVE);
        
        """.trimMargin()


    fun Cursor.statesCardId(): Long { return long(STATES_CARD_ID) }

    fun Cursor.statesExerciseId(): ExerciseId {
        val value = string(STATES_EXERCISE)
        return ExerciseId.fromValue(value)
    }
    fun Cursor.statesDateDue(): DateTime { return date(STATES_DUE_DATE) }
    fun Cursor.statesInterval(): Int { return int(STATES_INTERVAL) }
    fun Cursor.statesIntervalOrNeverScheduled(): Int { return intOrNull(STATES_INTERVAL) ?: INTERVAL_NEVER_SCHEDULED }
    fun Cursor.statesHasSavedExerciseState(): Boolean { return !isNull(STATES_EXERCISE) }
    fun Cursor.exerciseState(): ExerciseState {
        return ExerciseState(statesDateDue(), statesInterval())
    }

    fun createAllLearningProgressContentValues(
            cardId: Long, learningProgress: LearningProgress): List<ContentValues> {
        return learningProgress.states.map {
            (exerciseId, _) -> createCardStateContentValues(cardId, exerciseId, learningProgress)
        }
    }

    fun createCardStateContentValues(cardId: Long, exerciseId: ExerciseId, learningProgress: LearningProgress): ContentValues {
        val state = learningProgress.stateFor(exerciseId)
        return createCardStateContentValues(cardId, exerciseId, state.due, state.interval)
    }

    fun createCardStateContentValues(cardId: Long, exerciseId: ExerciseId, due: DateTime, interval: Int): ContentValues {
        val cv = ContentValues()
        cv.put(STATES_CARD_ID, cardId)
        cv.put(STATES_EXERCISE, exerciseId.value)
        cv.put(STATES_DUE_DATE, due)
        cv.put(STATES_INTERVAL, interval)
        cv.put(STATES_IS_ACTIVE, STATES__CARD_ACTIVE)
        return cv
    }
}