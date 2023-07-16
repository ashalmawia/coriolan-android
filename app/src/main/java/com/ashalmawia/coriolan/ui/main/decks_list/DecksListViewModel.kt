package com.ashalmawia.coriolan.ui.main.decks_list

import androidx.lifecycle.ViewModel
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

class DecksListViewModel(
        private val domainId: Long,
        private val repository: Repository,
        private val preferences: Preferences,
        private val studyTargetsResolver: StudyTargetsResolver
) : ViewModel() {

    val domain: Domain by lazy { repository.domainById(domainId)!! }

    fun decksList(): List<DeckListItem> {
        val decks = repository.allDecksWithPendingCounts(domain, today())
        val studyTargets = studyTargetsResolver.defaultStudyTargets(today())
        return convertDecksToListItems(decks, studyTargets)
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

    fun totalCounts(item: DeckListItem) = repository.deckPendingCountsMix(item.deck, today())

    fun deckTotal(item: DeckListItem) = repository.deckStats(item.deck)[CardTypeFilter.BOTH]!!.total

    fun defaultStudyTargets() = studyTargetsResolver.defaultStudyTargets(today())
}