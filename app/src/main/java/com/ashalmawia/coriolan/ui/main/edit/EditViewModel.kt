package com.ashalmawia.coriolan.ui.main.edit

import androidx.lifecycle.ViewModel
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Deck

class EditViewModel(
        domainId: Long,
        private val repository: Repository
) : ViewModel() {

    private val domain = repository.domainById(domainId)!!

    var view: EditView? = null

    fun start(view: EditView) {
        this.view = view
    }

    fun finish() {
        this.view = null
    }

    fun refresh() {
        val decks = decks()
        view?.onDecksList(domain, decks)
    }

    private fun decks(): List<EditDeckListItem> {
        val decks = repository.allDecks(domain)
        val cardsCounts = repository.allDecksCardsCount(domain)
        return decks.map { EditDeckListItem(it, cardsCounts[it.id] ?: 0) }
    }

    fun deleteDeck(deck: Deck) {
        val deleted = repository.deleteDeck(deck)
        if (deleted) {
            refresh()
        } else {
            view?.showDeleteFailedDialog(deck)
        }
    }
}