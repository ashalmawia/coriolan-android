package com.ashalmawia.coriolan.ui.overview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.OverviewBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import org.koin.android.ext.android.inject

private const val KEY_DECK_ID = "deck_id"

class OverviewActivity : BaseActivity() {

    companion object {
        fun intent(context: Context, deck: Deck): Intent {
            val intent = Intent(context, OverviewActivity::class.java)
            return intent
                    .putExtra(KEY_DECK_ID, deck.id)
        }
    }

    private val repository: Repository by inject()

    private val views by lazy { OverviewBinding.inflate(layoutInflater) }
    private val adapter = OverviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        initialize()

        val deckId = intent.getLongExtra(KEY_DECK_ID, -1L)
        val deck = repository.deckById(deckId)
        bind(deck)
    }

    private fun initialize() {
        views.cardsList.layoutManager = LinearLayoutManager(this)
        views.cardsList.adapter = adapter
    }

    private fun bind(deck: Deck) {
        setUpToolbar(deck.name)

        val cards = repository.cardsOfDeck(deck)
        val list = buildCardsList(cards)
        adapter.setItems(list)
    }

    private fun buildCardsList(cards: List<Card>) = cards.map { FlexListItem.EntityItem(it) }
}