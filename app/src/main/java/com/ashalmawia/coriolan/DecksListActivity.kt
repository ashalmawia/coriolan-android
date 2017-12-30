package com.ashalmawia.coriolan

import android.content.Context

import kotlinx.android.synthetic.main.decks_list.*
import kotlinx.android.synthetic.main.app_toolbar.*

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.*
import com.ashalmawia.coriolan.learning.LearningFlow
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.util.inflate

class DecksListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decks_list)

        setSupportActionBar(toolbar)
        toolbar.setLogo(R.drawable.ic_logo_action_bar_with_text)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        initializeList()
    }

    private fun initializeList() {
        decksList.layoutManager = LinearLayoutManager(this)
        decksList.adapter = DecksAdapter(this)
        fetchData()
    }

    private fun fetchData() {
        (decksList.adapter as DecksAdapter).setData(decksList())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.decks_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_import_from_file -> importFromFile()
        }
        return true
    }

    private fun importFromFile() {
        DataImportFlow.start(this, DataImportFlow.default(), object : DataImportCallback {
            override fun onSuccess() {
                fetchData()
                Toast.makeText(this@DecksListActivity, R.string.import_success, Toast.LENGTH_SHORT).show()
            }

            override fun onError(message: String) {
                Toast.makeText(this@DecksListActivity, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun decksList(): List<Deck> {
        return DecksRegistry.allDecks(this)
    }
}

class DecksAdapter(private val context: Context) : RecyclerView.Adapter<DeckViewHolder>() {

    private val decks: MutableList<Deck> = mutableListOf()

    fun setData(data: List<Deck>) {
        decks.clear()
        decks.addAll(data)
        notifyDataSetChanged()
    }

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