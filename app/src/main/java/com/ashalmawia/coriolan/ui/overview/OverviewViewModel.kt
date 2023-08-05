package com.ashalmawia.coriolan.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel(
        private val deckId: DeckId,
        private val repository: Repository
) : ViewModel() {

    var view: OverviewView? = null

    private lateinit var deck: Deck
    private lateinit var allCards: List<CardItem>
    private lateinit var currentCards: List<CardItem>

    private var searchTerm: String = ""

    fun start(view: OverviewView) {
        this.view = view
        view.showLoading()

        val defaultSorting = OverviewSorting.default()
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                deck = repository.deckById(deckId)
            }
            withContext(Dispatchers.Main) {
                view.initialize(deck.name, defaultSorting)
            }
            withContext(Dispatchers.Default) {
                allCards = buildCardsList(repository.cardsOfDeck(deck))
            }
            withContext(Dispatchers.Main) {
                updateContent(sort(allCards, defaultSorting))
            }
        }
    }

    fun finish() {
        this.view = null
    }

    private fun buildCardsList(cards: List<Card>): List<CardItem> {
        return cards.map { FlexListItem.EntityItem(it) }
    }

    private fun sort(cards: List<CardItem>, sorting: OverviewSorting): List<CardItem> {
        return when (sorting) {
            OverviewSorting.DATE_ADDED_NEWEST_FIRST -> cards.sortedByDescending { it.entity.id.value }
            OverviewSorting.DATE_ADDED_OLDEST_FIRST -> cards.sortedBy { it.entity.id.value }
            OverviewSorting.ALPHABETICALLY_A_Z -> cards.sortedBy { it.entity.original.value }
            OverviewSorting.ALPHABETICALLY_Z_A -> cards.sortedByDescending { it.entity.original.value }
        }
    }

    fun searchTerm(term: String) {
        val view = view ?: return
        view.showLoading()

        val oldTerm = searchTerm
        val baseList = if (term.contains(oldTerm)) {
            currentCards
        } else {
            sort(allCards, view.selectedSorting())
        }

        updateContent(baseList.filter { it.entity.original.value.contains(term) })
        searchTerm = term
    }

    fun onSortingUpdated(sorting: OverviewSorting) {
        updateContent(sort(currentCards, sorting))
    }

    private fun updateContent(list: List<CardItem>) {
        currentCards = list
        view?.bindContent(list, allCards.size)
    }

    fun addCards() {
        view?.startAddCardsActivity(deck)
    }

    fun editDeck() {
        view?.startEditDeckActivity(deck)
    }
}