package com.ashalmawia.coriolan.ui.main.decks_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.prefs.Preferences
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.learning.StudyTargets
import com.ashalmawia.coriolan.learning.StudyTargetsResolver
import com.ashalmawia.coriolan.learning.TodayManager.today
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
) : ViewModel() {

    val domain: Domain by lazy { repository.domainById(domainId)!! }

    fun fetchDeckCardCounts(item: DeckListItem, update: (Counts, Int) -> Unit) {
        viewModelScope.launch {
            val totalCounts = totalCounts(item)
            val total = deckTotal(item)

            withContext(Dispatchers.Main) {
                update(totalCounts, total)
            }
        }
    }

    fun defaultStudyTargets() = studyTargetsResolver.defaultStudyTargets(today())

    fun fetchDecksList(update: (List<DeckListItem>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val decks = decksList()
                withContext(Dispatchers.Main) {
                    update(decks)
                }
            }
        }
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
}