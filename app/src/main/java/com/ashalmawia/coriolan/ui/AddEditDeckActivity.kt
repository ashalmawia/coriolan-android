package com.ashalmawia.coriolan.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.errors.Errors
import kotlinx.android.synthetic.main.button_bar.*
import kotlinx.android.synthetic.main.create_deck.*
import org.koin.android.ext.android.inject

private const val TAG = "AddEditDeckActivity"

private const val EXTRA_DECK_ID = "deck_id"

class AddEditDeckActivity : BaseActivity() {

    private var deck: Deck? = null

    private val repository: Repository by inject()
    private val decksRegistry: DecksRegistry by domainScope().inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_deck)
        initialize()

        val deckId = intent.getLongExtra(EXTRA_DECK_ID, -1L)
        val editMode = deckId != -1L
        if (editMode) {
            setTitle(R.string.edit_deck__title)
            extractData(deckId)
        } else {
            setTitle(R.string.add_deck__title)
        }
    }

    private fun initialize() {
        buttonCancel.setOnClickListener { finish() }
        buttonOk.setOnClickListener { saveWithValidation() }
    }

    private fun extractData(deckId: Long) {
        val deck = repository.deckById(deckId, decksRegistry.domain)
        if (deck != null) {
            this.deck = deck
            prefillValues(deck)
        } else {
            finishWithError("deck with id $deckId was not in the repository")
        }
    }

    private fun prefillValues(deck: Deck) {
        nameField.setText(deck.name)
        buttonOk.setText(R.string.button_save)
    }

    private fun saveWithValidation() {
        val name = nameField.text.toString()
        if (!validate(name)) {
            return
        }

        val deck = deck
        if (deck != null) {
            updateDeckAndFinish(deck, name)
        } else {
            createDeckAndFinish(name)
        }
    }

    private fun createDeckAndFinish(name: String) {
        try {
            decksRegistry.addDeck(name)
            finishOk()
        } catch (e: DataProcessingException) {
            showError(getString(R.string.add_deck__failed_already_exists, name))
        } catch (e: Exception) {
            Errors.error(TAG, e)
            showError(getString(R.string.add_deck__failed_to_create, name))
        }
    }

    private fun updateDeckAndFinish(deck: Deck, name: String) {
        try {
            if (name != deck.name) {
                decksRegistry.updateDeck(deck, name)
            }
            finishOk()
        } catch (e: DataProcessingException) {
            showError(getString(R.string.add_deck__failed_already_exists, name))
        } catch (e: Exception) {
            Errors.error(TAG, e)
            showError(getString(R.string.add_deck__failed_to_update))
        }
    }

    private fun validate(name: String): Boolean {
        if (name.isBlank()) {
            showError(getString(R.string.add_deck__empty_name))
            return false
        }

        return true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun finishWithError(message: String) {
        Errors.illegalState(TAG, message)
        finish()
    }

    companion object {
        fun create(context: Context): Intent {
            return Intent(context, AddEditDeckActivity::class.java)
        }

        fun edit(context: Context, deck: Deck): Intent {
            val intent = Intent(context, AddEditDeckActivity::class.java)
            intent.putExtra(EXTRA_DECK_ID, deck.id)
            return intent
        }
    }
}