package com.ashalmawia.coriolan.data.backup.logbook

import com.ashalmawia.coriolan.data.logbook.BackupableLogbook
import com.ashalmawia.coriolan.data.logbook.sqlite.SqliteLogbook
import com.ashalmawia.coriolan.data.storage.provideLogbookHelper
import com.ashalmawia.coriolan.learning.exercise.CardAction
import com.ashalmawia.coriolan.learning.exercise.ExerciseId
import com.ashalmawia.coriolan.learning.mockToday

fun createNonEmptyLogbookWithMockData(): BackupableLogbook {
    val logbook = SqliteLogbook(provideLogbookHelper())
    val today = mockToday()

    logbook.incrementCardActions(today.minusDays(5), ExerciseId.TEST, 1L, CardAction.NEW_CARD_FIRST_SEEN)
    logbook.incrementCardActions(today.minusDays(5), ExerciseId.TEST, 1L, CardAction.NEW_CARD_FIRST_SEEN)
    logbook.incrementCardActions(today.minusDays(5), ExerciseId.TEST, 1L, CardAction.CARD_RELEARNED)
    logbook.incrementCardActions(today.minusDays(5), ExerciseId.FLASHCARDS, 1L, CardAction.NEW_CARD_FIRST_SEEN)

    logbook.incrementCardActions(today.minusDays(3), ExerciseId.PREVIEW, 2L, CardAction.NEW_CARD_FIRST_SEEN)
    logbook.incrementCardActions(today.minusDays(3), ExerciseId.PREVIEW, 2L, CardAction.CARD_REVIEWED)
    logbook.incrementCardActions(today.minusDays(3), ExerciseId.TEST, 1L, CardAction.CARD_RELEARNED)
    logbook.incrementCardActions(today.minusDays(3), ExerciseId.FLASHCARDS, 1L, CardAction.CARD_REVIEWED)

    return logbook
}