package com.ashalmawia.coriolan.ui.overview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.OverviewBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.view.visible
import org.koin.android.ext.android.inject

private const val KEY_DECK_ID = "deck_id"

private typealias CardItem = FlexListItem.EntityItem<Card>

class OverviewActivity : BaseActivity(), OverviewAdapter.Callback, SearchView.OnQueryTextListener {

    companion object {
        fun intent(context: Context, deck: Deck): Intent {
            val intent = Intent(context, OverviewActivity::class.java)
            return intent
                    .putExtra(KEY_DECK_ID, deck.id)
        }
    }

    private val repository: Repository by inject()

    private val views by lazy { OverviewBinding.inflate(layoutInflater) }
    private val adapter = OverviewAdapter(this)

    private val deck by lazy {
        val deckId = intent.getLongExtra(KEY_DECK_ID, -1L)
        repository.deckById(deckId)
    }
    private val allCards by lazy {
        val list = buildCardsList(repository.cardsOfDeck(deck))
        currentCards = list
        list
    }
    private lateinit var currentCards: List<CardItem>
    private var searchTerm: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        initialize()
    }

    private fun initialize() {
        views.cardsList.layoutManager = LinearLayoutManager(this)
        views.cardsList.adapter = adapter

        val count = allCards.size
        val subtitle = resources.getQuantityString(R.plurals.cards_count, count, count)
        setUpToolbar(deck.name, subtitle)
        setUpSorting()
    }

    private fun setUpSorting() {
        val values = OverviewSorting.values().map { getString(it.titleRes) }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, values)
        views.sortingSpinner.adapter = spinnerAdapter
        views.sortingSpinner.setSelection(OverviewSorting.values().indexOf(OverviewSorting.default()))
        views.sortingSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val sorting = OverviewSorting.values()[position]
                bind(sorting)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onStart() {
        super.onStart()

        bind(selectedSorting())
    }

    private fun selectedSorting(): OverviewSorting {
        return if (views.sortingSpinner.selectedItemPosition >= 0) {
            val position = views.sortingSpinner.selectedItemPosition
            OverviewSorting.values()[position]
        } else {
            OverviewSorting.default()
        }
    }

    private fun bind(sorting: OverviewSorting) {
        views.sortingSpinner.setSelection(OverviewSorting.values().indexOf(sorting))
        val cards = if (searchTerm.isEmpty()) allCards else currentCards
        updateContent(sort(cards, sorting))
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

    override fun onCardClicked(card: Card) {
        val intent = AddEditCardActivity.edit(this, card)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.overview, menu)
        initializeSearch(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initializeSearch(menu: Menu) {
        val searchItem = menu.findItem(R.id.toolbar_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        searchTerm(query)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        searchTerm(newText)
        return true
    }

    private fun searchTerm(term: String) {
        val oldTerm = searchTerm
        val baseList = if (term.contains(oldTerm)) {
            currentCards
        } else {
            sort(allCards, selectedSorting())
        }

        updateContent(baseList.filter { it.entity.original.value.contains(term) })
        searchTerm = term
    }

    private fun updateContent(list: List<CardItem>) {
        currentCards = list

        if (allCards.isEmpty()) {
            views.emptyDeckLabel.setText(R.string.overview__empty_deck)
            views.emptyDeckLabel.visible = true
            views.cardsList.visible = false
        } else if (currentCards.isEmpty()) {
            views.emptyDeckLabel.setText(R.string.overview__empty_result)
            views.emptyDeckLabel.visible = true
            views.cardsList.visible = false
        } else {
            views.emptyDeckLabel.visible = false
            views.cardsList.visible = true

            adapter.setItems(list)
        }
    }
}