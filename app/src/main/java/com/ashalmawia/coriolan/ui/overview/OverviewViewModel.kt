package com.ashalmawia.coriolan.ui.overview

import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem

class OverviewViewModel(
        private val deckId: Long,
        private val repository: Repository
) : ViewModel() {

    var view: OverviewView? = null

    private lateinit var deck: Deck
    private lateinit var allCards: List<CardItem>
    private lateinit var currentCards: List<CardItem>

    private var searchTerm: String = ""

    fun start(view: OverviewView) {
        this.view = view

        deck = fetchDeck()
        allCards = fetchCards()

        val defaultSorting = OverviewSorting.default()
        view.initialize(deck.name, defaultSorting)
    }

    fun finish() {
        this.view = null
    }

    private fun fetchDeck(): Deck {

        return repository.deckById(deckId)
    }

    private fun fetchCards(): List<CardItem> {
        val list = buildCardsList(repository.cardsOfDeck(deck))
        currentCards = list
        return list
    }

    private fun buildCardsList(cards: List<Card>): List<CardItem> {
        return cards.map { FlexListItem.EntityItem(it) }
    }

    private fun sort(cards: List<CardItem>, sorting: OverviewSorting): List<CardItem> {
        return when (sorting) {
            OverviewSorting.DATE_ADDED_NEWEST_FIRST -> cards.sortedByDescending { it.entity.id }
            OverviewSorting.DATE_ADDED_OLDEST_FIRST -> cards.sortedBy { it.entity.id }
            OverviewSorting.ALPHABETICALLY_A_Z -> cards.sortedBy { it.entity.original.value }
            OverviewSorting.ALPHABETICALLY_Z_A -> cards.sortedByDescending { it.entity.original.value }
        }
    }

    fun searchTerm(term: String) {
        val view = view ?: return

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
        val cards = if (searchTerm.isEmpty()) allCards else currentCards
        updateContent(sort(cards, sorting))
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