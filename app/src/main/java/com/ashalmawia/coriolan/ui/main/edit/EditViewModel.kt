package com.ashalmawia.coriolan.ui.main.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditViewModel(
        private val domainId: DomainId,
        private val repository: Repository
) : ViewModel() {

    var view: EditView? = null

    fun start(view: EditView) {
        this.view = view
        view.showLoading()
    }

    fun finish() {
        this.view = null
    }

    private fun domain() = repository.domainById(domainId)!!

    fun refresh() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val domain = domain()
                val decks = decks(domain)
                withContext(Dispatchers.Main) {
                    view?.onDecksList(domain, decks)
                }
            }
        }
    }

    private fun decks(domain: Domain): List<EditDeckListItem> {
        val decks = repository.allDecks(domain)
        val cardsCounts = repository.allDecksCardsCount(domain)
        return decks.map { EditDeckListItem(it, cardsCounts[it.id] ?: 0) }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val deleted = repository.deleteDeck(deck)
                withContext(Dispatchers.IO) {
                    if (deleted) {
                        refresh()
                    } else {
                        view?.showDeleteFailedDialog(deck)
                    }
                }
            }
        }
    }
}