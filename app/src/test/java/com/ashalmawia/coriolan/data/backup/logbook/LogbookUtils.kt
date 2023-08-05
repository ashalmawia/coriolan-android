package com.ashalmawia.coriolan.data.backup.logbook

import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbook
import com.ashalmawia.coriolan.data.storage.provideLogbookHelper
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday
import com.ashalmawia.coriolan.util.asDeckId

fun createNonEmptyLogbookWithMockData(): BackupableLogbook {
    val logbook = SqliteLogbook(provideLogbookHelper())
    val today = mockToday()

    val deckId1 = 1L.asDeckId()
    val deckId2 = 2L.asDeckId()

    logbook.incrementCardActions(today.minusDays(5), ExerciseId.TEST, deckId1, CardAction.NEW_CARD_FIRST_SEEN)
    logbook.incrementCardActions(today.minusDays(5), ExerciseId.TEST, deckId1, CardAction.NEW_CARD_FIRST_SEEN)
    logbook.incrementCardActions(today.minusDays(5), ExerciseId.TEST, deckId1, CardAction.CARD_RELEARNED)
    logbook.incrementCardActions(today.minusDays(5), ExerciseId.FLASHCARDS, deckId1, CardAction.NEW_CARD_FIRST_SEEN)

    logbook.incrementCardActions(today.minusDays(3), ExerciseId.PREVIEW, deckId2, CardAction.NEW_CARD_FIRST_SEEN)
    logbook.incrementCardActions(today.minusDays(3), ExerciseId.PREVIEW, deckId2, CardAction.CARD_REVIEWED)
    logbook.incrementCardActions(today.minusDays(3), ExerciseId.TEST, deckId1, CardAction.CARD_RELEARNED)
    logbook.incrementCardActions(today.minusDays(3), ExerciseId.FLASHCARDS, deckId1, CardAction.CARD_REVIEWED)

    return logbook
}