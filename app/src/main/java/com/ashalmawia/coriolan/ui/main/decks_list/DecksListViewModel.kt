package com.ashalmawia.coriolan.ui.main.decks_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.StudyTargetsResolver
import com.ashalmawia.coriolan.learning.TodayChangeListener
import com.ashalmawia.coriolan.learning.TodayManager
import com.ashalmawia.coriolan.learning.mutation.StudyOrder
import com.ashalmawia.coriolan.model.Counts
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.PendingCardsCount
import com.ashalmawia.coriolan.ui.learning.CardTypeFilter
import com.ashalmawia.coriolan.util.orMax
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecksListViewModel(
        private val domainId: Long,
        private val repository: Repository,
        private val preferences: Preferences,
        private val studyTargetsResolver: StudyTargetsResolver
) : ViewModel(), TodayChangeListener, DeckListAdapterListener {

    val domain: Domain by lazy { repository.domainById(domainId)!! }

    private fun today() = TodayManager.today()

    private var view: DecksListView? = null

    fun onStart(view: DecksListView) {
        TodayManager.register(this)
        this.view = view
    }

    fun onResume() {
        fetchData()
    }

    fun onStop() {
        TodayManager.unregister(this)
        view = null
    }

    private fun fetchData() {
        val view = view ?: return

        view.showLoading()
        fetchDecksList { decks ->
            view.setDecks(decks)
            view.hideLoading()
        }
    }

    private fun fetchDeckCardCounts(item: DeckListItem, update: (Counts, Int) -> Unit) {
        viewModelScope.launch {
            val totalCounts = totalCounts(item)
            val total = deckTotal(item)

            withContext(Dispatchers.Main) {
                update(totalCounts, total)
            }
        }
    }

    private fun defaultStudyTargets() = studyTargetsResolver.defaultStudyTargets(today())

    private fun fetchDecksList(update: (List<DeckListItem>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val decks = decksList()
                withContext(Dispatchers.Main) {
                    update(decks)
                }
            }
        }
    }

    private fun beginStudy(item: DeckListItem, studyOrder: StudyOrder) {
        if (item.hasPending) {
            view?.launchLearning(item, studyOrder, defaultStudyTargets())
        } else {
            fetchDeckCardCounts(item) { counts, total ->
                if (total == 0) {
                    view?.showDeckEmptyMessage(item)
                } else if (counts.isAnythingPending()) {
                    view?.showSuggestStudyMoreDialog(item, repository, today())
                } else {
                    view?.showNothingToLearnTodayDialog()
                }
            }
        }
    }

    override fun onDayChanged() {
        // to update pending counters on deck items
        fetchData()
    }

    private suspend fun decksList(): List<DeckListItem> = withContext(Dispatchers.Default) {
        val decks = repository.allDecksWithPendingCounts(domain, today())
        val studyTargets = studyTargetsResolver.defaultStudyTargets(today())
        convertDecksToListItems(decks, studyTargets)
    }

    private fun hasPendingCardsForToday(counts: Counts, studyTargets: StudyTargets): Boolean {
        if (counts.relearn > 0) return true
        val hasPendingNew = counts.new > 0 && studyTargets.new.orMax() > 0
        val hasPendingReview = counts.review > 0 && studyTargets.review.orMax() > 0
        return hasPendingNew || hasPendingReview
    }

    private fun convertDecksToListItems(decks: Map<Deck, PendingCardsCount>, studyTargets: StudyTargets): List<DeckListItem> {
        return if (preferences.mixForwardAndReverse) {
            decks.map { (deck, counts) ->
                DeckListItem(deck, CardTypeFilter.BOTH, hasPendingCardsForToday(counts.total, studyTargets))
            }
        } else {
            decks.flatMap { (deck, counts) -> listOf(
                    DeckListItem(deck, CardTypeFilter.FORWARD, hasPendingCardsForToday(counts.forward, studyTargets)),
                    DeckListItem(deck, CardTypeFilter.REVERSE, hasPendingCardsForToday(counts.reverse, studyTargets))
            ) }
        }
    }

    private suspend fun totalCounts(item: DeckListItem) = withContext(Dispatchers.Default) {
        repository.deckPendingCountsMix(item.deck, today())
    }

    private suspend fun deckTotal(item: DeckListItem) = withContext(Dispatchers.Default) {
        repository.deckStats(item.deck)[CardTypeFilter.BOTH]!!.total
    }

    override fun onDeckItemClicked(deck: DeckListItem) {
        beginStudy(deck, StudyOrder.default())
    }

    override fun onOptionStudyStraightforward(deck: DeckListItem) {
        beginStudy(deck, StudyOrder.ORDER_ADDED)
    }

    override fun onOptionStudyRandom(deck: DeckListItem) {
        beginStudy(deck, StudyOrder.RANDOM)
    }

    override fun onOptionNewestFirst(deck: DeckListItem) {
        beginStudy(deck, StudyOrder.NEWEST_FIRST)
    }

    override fun onOptionStudyMore(deck: DeckListItem) {
        view?.showLearnMoreDialog(deck, repository, today())
    }

    override fun onOptionDetails(deck: DeckListItem) {
        view?.showDeckDetailsDialog(deck, repository)
    }
}