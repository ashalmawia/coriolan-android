package com.ashalmawia.coriolan

import kotlinx.android.synthetic.main.deck_content.*

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashalmawia.coriolan.data.CardsStorage
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.util.inflate

class DeckContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.deck_content)

        initializeList()
    }

    private fun initializeList() {
        cardsList.layoutManager = LinearLayoutManager(this)
        cardsList.adapter = CardListAdapter(CardsStorage.cardsByDeckName("Default"))
    }
}

class CardListAdapter(val cards: List<Card>) : RecyclerView.Adapter<CardViewHolder>() {

    override fun onBindViewHolder(holder: CardViewHolder?, position: Int) {
        val item = cards[position]
        (holder!!.itemView as TextView).text = item.original.value + " - " + item.translations[0].value
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardViewHolder {
        val view = parent!!.inflate(R.layout.deck_list_item, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cards.size
    }
}

class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)