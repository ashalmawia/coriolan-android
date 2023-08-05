package com.ashalmawia.coriolan.ui.commons

import android.content.Context
import androidx.appcompat.widget.AppCompatSpinner
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.errors.Errors

class DeckSelector(context: Context, attributeSet: AttributeSet?) : AppCompatSpinner(context, attributeSet) {

    fun initialize(decks: List<Deck>) {
        adapter = DecksSelectorAdapter(context, decks)
        setSelection(0, false)
    }

    fun selectDeckWithId(id: DeckId) {
        val position = (adapter as DecksSelectorAdapter).positionOfDeck { it.id == id }
        setSelection(position)
    }

    fun selectedDeck() = selectedItem as Deck
}

private class DecksSelectorAdapter(val context: Context, val decks: List<Deck>) : BaseAdapter() {

    private val TAG = DecksSelectorAdapter::class.java.simpleName

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: createView(parent)
        bindView(view, getItem(position))
        return view
    }

    private fun createView(parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false)
    }

    private fun bindView(view: View, item: Deck) {
        (view as TextView).text = item.name
    }

    override fun getItem(position: Int): Deck {
        return decks[position]
    }

    override fun getItemId(position: Int): Long {
        return decks[position].id.value
    }

    override fun getCount(): Int {
        return decks.size
    }

    fun positionOfDeck(selector: (Deck) -> Boolean): Int {
        val index = decks.indexOfFirst(selector)
        return if (index == -1) {
            Errors.illegalState(TAG, "deck not found in the adapter, falling back to default")
            0
        } else {
            index
        }
    }
}