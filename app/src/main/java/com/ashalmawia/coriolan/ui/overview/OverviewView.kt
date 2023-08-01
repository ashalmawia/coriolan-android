package com.ashalmawia.coriolan.ui.overview

import android.view.Menu
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.OverviewBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditDeckActivity
import com.ashalmawia.coriolan.ui.view.setOnItemSelectedListener

interface OverviewView {
    fun initialize(deckName: String, defaultSorting: OverviewSorting)
    fun initializeSearch(menu: Menu)
    fun bindContent(cards: List<CardItem>, totalCardsCount: Int)
    fun selectedSorting(): OverviewSorting
    fun startAddCardsActivity(deck: Deck)
    fun startEditDeckActivity(deck: Deck)
    fun showLoading()
}

class OverviewViewImpl(
        private val views: OverviewBinding,
        private val activity: BaseActivity,
        private val viewModel: OverviewViewModel
) : OverviewView, OverviewAdapter.Callback, OnQueryTextListener {

    private val adapter = OverviewAdapter(this)

    init {
        views.cardsList.layoutManager = LinearLayoutManager(activity)
        views.cardsList.adapter = adapter
    }

    override fun initialize(deckName: String, defaultSorting: OverviewSorting) {
        initializeSorting(defaultSorting)
        activity.setUpToolbar(deckName, "")
        activity.showLoading()
    }

    private fun initializeSorting(defaultSorting: OverviewSorting) {
        val values = OverviewSorting.values().map { activity.getString(it.titleRes) }
        val spinnerAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, values)
        views.sortingSpinner.adapter = spinnerAdapter
        views.sortingSpinner.onItemSelectedListener = null
        views.sortingSpinner.setSelection(OverviewSorting.values().indexOf(defaultSorting))
    }

    private fun initializeSortingListener() {
        views.sortingSpinner.setOnItemSelectedListener { position ->
            val sorting = OverviewSorting.values()[position]
            viewModel.onSortingUpdated(sorting)
        }
    }

    override fun onCardClicked(card: Card) {
        val intent = AddEditCardActivity.edit(activity, card)
        activity.startActivity(intent)
    }

    override fun initializeSearch(menu: Menu) {
        val searchItem = menu.findItem(R.id.toolbar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        viewModel.searchTerm(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        viewModel.searchTerm(newText)
        return true
    }

    override fun startAddCardsActivity(deck: Deck) {
        val intent = AddEditCardActivity.add(activity, deck)
        activity.startActivity(intent)
    }

    override fun startEditDeckActivity(deck: Deck) {
        val intent = AddEditDeckActivity.edit(activity, deck)
        activity.startActivity(intent)
    }

    override fun selectedSorting(): OverviewSorting {
        return if (views.sortingSpinner.selectedItemPosition >= 0) {
            val position = views.sortingSpinner.selectedItemPosition
            OverviewSorting.values()[position]
        } else {
            OverviewSorting.default()
        }
    }

    private fun updateToolbarSubtitle(totalCardsCount: Int) {
        val subtitle = activity.resources.getQuantityString(R.plurals.cards_count, totalCardsCount, totalCardsCount)
        activity.updateToolbarSubtitle(subtitle)
    }

    override fun bindContent(cards: List<CardItem>, totalCardsCount: Int) {
        activity.hideLoading()
        updateToolbarSubtitle(totalCardsCount)

        if (totalCardsCount == 0) {
            views.emptyDeckLabel.setText(R.string.overview__empty_deck)
            views.emptyDeckLabel.isVisible = true
            views.cardsList.isVisible = false
        } else if (cards.isEmpty()) {
            views.emptyDeckLabel.setText(R.string.overview__empty_result)
            views.emptyDeckLabel.isVisible = true
            views.cardsList.isVisible = false
        } else {
            views.emptyDeckLabel.isVisible = false
            views.cardsList.isVisible = true

            adapter.setItems(cards)
        }

        initializeSortingListener()
    }

    override fun showLoading() {
        activity.showLoading()
    }
}