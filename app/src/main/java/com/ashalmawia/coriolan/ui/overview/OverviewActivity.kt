package com.ashalmawia.coriolan.ui.overview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.OverviewBinding
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.util.viewModelBuilder
import org.koin.android.ext.android.get

private const val KEY_DECK_ID = "deck_id"

typealias CardItem = FlexListItem.EntityItem<Card>

class OverviewActivity : BaseActivity() {

    companion object {
        fun intent(context: Context, deck: Deck): Intent {
            val intent = Intent(context, OverviewActivity::class.java)
            return intent
                    .putExtra(KEY_DECK_ID, deck.id)
        }
    }

    private val views by lazy { OverviewBinding.inflate(layoutInflater) }
    private val viewModel by viewModelBuilder {
        val deckId = intent.getLongExtra(KEY_DECK_ID, -1L)
        OverviewViewModel(deckId, get())
    }
    private val view: OverviewView by lazy { OverviewViewImpl(views, this, viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
    }

    override fun onResume() {
        super.onResume()
        viewModel.start(view)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.overview, menu)
        view.initializeSearch(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_card -> {
                viewModel.addCards()
                return true
            }
            R.id.edit_deck -> {
                viewModel.editDeck()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}