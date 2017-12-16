package com.ashalmawia.coriolan

import android.content.Context

import kotlinx.android.synthetic.main.decks_list.*

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashalmawia.coriolan.data.DecksStorage
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.util.inflate

class DecksListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decks_list)

        initializeList()
    }

    private fun initializeList() {
        decksList.layoutManager = LinearLayoutManager(this)
        decksList.adapter = DecksAdapter(this, decksList())
    }

    private fun decksList(): List<Deck> {
        return DecksStorage.allDecks()
    }
}

class DecksAdapter(
        private val context: Context,
        private val decks: List<Deck>) : RecyclerView.Adapter<DeckViewHolder>() {

    override fun getItemCount(): Int {
        return decks.size
    }

    override fun onBindViewHolder(holder: DeckViewHolder?, position: Int) {
        val item = decks[position]
        (holder!!.itemView as TextView).text = item.name
        holder.itemView.setOnClickListener { showContent(item) }
    }

    private fun showContent(deck: Deck) {
        LearningFlow.initiateDefault(deck).start(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DeckViewHolder {
        val view = parent!!.inflate(R.layout.deck_list_item, false)
        return DeckViewHolder(view)
    }
}

class DeckViewHolder(view :View) : RecyclerView.ViewHolder(view)