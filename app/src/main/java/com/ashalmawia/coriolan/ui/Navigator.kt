package com.ashalmawia.coriolan.ui

import android.app.Dialog
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain

interface Navigator {

    fun openCreateDomainScreen(isFirstStart: Boolean)

    fun openDomainsList()

    fun openDomain(domain: Domain)

    fun openDomainWithStack(domain: Domain)

    fun openAddDeckScreen()

    fun openEditDeckScreen(deck: Deck)

    fun openAddCardScreen(deck: Deck)

    fun openEditCardScreen(card: Card)

    fun openCreateBackupScreen()

    fun openRestoreFromBackupScreen()

    fun createIncreaseLimitsDialog(deck: Deck, exercise: Exercise<*, *>, date: LearningDay): Dialog

    fun createDeckDetailsDialog(deck: Deck, exercise: Exercise<*, *>, date: LearningDay): Dialog

    fun createDebugIncreaseDateDialog(): Dialog

    fun openSettings()
}