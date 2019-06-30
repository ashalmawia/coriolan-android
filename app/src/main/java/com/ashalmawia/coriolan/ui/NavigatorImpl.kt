package com.ashalmawia.coriolan.ui

import android.app.Dialog
import android.content.Intent
import com.ashalmawia.coriolan.REQUEST_CODE_EDIT_CARD
import com.ashalmawia.coriolan.data.backup.ui.BackupActivity
import com.ashalmawia.coriolan.data.backup.ui.RestoreFromBackupActivity
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.debug.DebugIncreaseDateDialog
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.LearningDay
import com.ashalmawia.coriolan.learning.TodayProvider
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.domains_list.DomainsListActivity
import com.ashalmawia.coriolan.ui.settings.SettingsActivity

class NavigatorImpl(
        private val activity: BaseActivity,
        private val repository: Repository,
        private val preferences: Preferences,
        private val todayProvider: TodayProvider
) : Navigator {

    private fun startActivity(intent: Intent) = activity.startActivity(intent)

    override fun openCreateDomainScreen(isFirstStart: Boolean) {
        val intent = CreateDomainActivity.intent(activity, true)
        startActivity(intent)
    }

    override fun openDomainsList() {
        val intent = Intent(activity, DomainsListActivity::class.java)
        startActivity(intent)
    }

    override fun openDomainWithStack(domain: Domain) {
        (activity as? StartActivity)?.finish()

        startActivity(DomainsListActivity.intent(activity))
        startActivity(DomainActivity.intent(activity, domain))

        activity.overridePendingTransition(0, 0)
    }

    override fun openDomain(domain: Domain) {
        startActivity(DomainActivity.intent(activity, domain))
    }

    override fun openAddDeckScreen() {
        val intent = AddEditDeckActivity.create(activity)
        startActivity(intent)
    }

    override fun openEditDeckScreen(deck: Deck) {
        val intent = AddEditDeckActivity.edit(activity, deck)
        startActivity(intent)
    }

    override fun openAddCardScreen(deck: Deck) {
        val intent = AddEditCardActivity.add(activity, deck)
        startActivity(intent)
    }

    override fun openEditCardScreen(card: Card) {
        val intent = AddEditCardActivity.edit(activity, card)
        startActivityForResult(intent, REQUEST_CODE_EDIT_CARD)
    }

    override fun openCreateBackupScreen() {
        val intent = BackupActivity.intent(activity)
        startActivity(intent)
    }

    override fun openRestoreFromBackupScreen() {
        val intent = RestoreFromBackupActivity.intent(activity)
        startActivity(intent)
    }

    override fun createIncreaseLimitsDialog(deck: Deck, exercise: Exercise<*, *>, date: LearningDay): Dialog {
        return IncreaseLimitsDialog(activity, deck, exercise, date, repository, preferences, todayProvider).build()
    }

    override fun createDeckDetailsDialog(deck: Deck, exercise: Exercise<*, *>, date: LearningDay): Dialog {
        return DeckDetailsDialog(activity, deck, exercise, date, repository)
    }

    override fun createDebugIncreaseDateDialog(): Dialog {
        return DebugIncreaseDateDialog(activity, todayProvider)
    }

    override fun openSettings() {
        val intent = SettingsActivity.intent(activity)
        activity.startActivity(intent)
    }
}