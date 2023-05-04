package com.ashalmawia.coriolan.ui.overview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.OverviewBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import org.koin.android.ext.android.inject

private const val KEY_DECK_ID = "deck_id"

class OverviewActivity : BaseActivity(), OverviewAdapter.Callback {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        initialize()
    }

    private fun initialize() {
        views.cardsList.layoutManager = LinearLayoutManager(this)
        views.cardsList.adapter = adapter
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
                bind(deck, sorting)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun onStart() {
        super.onStart()

        bind(deck, selectedSorting())
    }

    private fun selectedSorting(): OverviewSorting {
        if (views.sortingSpinner.selectedItemPosition >= 0) {
            val position = views.sortingSpinner.selectedItemPosition
            return OverviewSorting.values()[position]
        } else {
            return OverviewSorting.default()
        }
    }

    private fun bind(deck: Deck, sorting: OverviewSorting) {
        setUpToolbar(deck.name)

        views.sortingSpinner.setSelection(OverviewSorting.values().indexOf(sorting))

        val cards = repository.cardsOfDeck(deck)
        val list = buildCardsList(cards, sorting)
        adapter.setItems(list)
    }

    private fun buildCardsList(cards: List<Card>, sorting: OverviewSorting): List<FlexListItem> {
        return sort(cards, sorting).map { FlexListItem.EntityItem(it) }
    }

    private fun sort(cards: List<Card>, sorting: OverviewSorting): List<Card> {
        return when (sorting) {
            OverviewSorting.DATE_ADDED_NEWEST_FIRST -> cards.sortedByDescending { it.id }
            OverviewSorting.DATE_ADDED_OLDEST_FIRST -> cards.sortedBy { it.id }
            OverviewSorting.ALPHABETICALLY_A_Z -> cards.sortedBy { it.original.value }
            OverviewSorting.ALPHABETICALLY_Z_A -> cards.sortedByDescending { it.original.value }
        }
    }

    override fun onCardClicked(card: Card) {
        val intent = AddEditCardActivity.edit(this, card)
        startActivity(intent)
    }
}