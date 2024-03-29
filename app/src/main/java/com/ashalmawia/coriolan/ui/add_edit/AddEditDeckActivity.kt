package com.ashalmawia.coriolan.ui.add_edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.storage.DataProcessingException
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.CreateDeckBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.DeckId
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.model.DomainId
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.util.requireSerializable
import com.ashalmawia.coriolan.ui.util.serializable
import com.ashalmawia.errors.Errors
import org.koin.android.ext.android.inject

private const val TAG = "AddEditDeckActivity"

private const val EXTRA_DOMAIN_ID = "domain_id"
private const val EXTRA_DECK_ID = "deck_id"

class AddEditDeckActivity : BaseActivity() {
    
    private val views by lazy { CreateDeckBinding.inflate(layoutInflater) }

    private var deck: Deck? = null

    private val repository: Repository by inject()
    private val decksRegistry: DecksRegistry by inject()

    private val domain: Domain by lazy {
        val domainId = intent.requireSerializable<DomainId>(EXTRA_DOMAIN_ID)
        repository.domainById(domainId)!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        initialize()

        val deckId = intent.serializable<DeckId>(EXTRA_DECK_ID)
        if (deckId != null) {
            setTitle(R.string.edit_deck__title)
            extractData(deckId)
        } else {
            setTitle(R.string.add_deck__title)
        }
    }

    private fun initialize() {
        views.buttonBar.buttonCancel.setOnClickListener { finish() }
        views.buttonBar.buttonOk.setOnClickListener { saveWithValidation() }
    }

    private fun extractData(deckId: DeckId) {
        val deck = repository.deckById(deckId)
        this.deck = deck
        prefillValues(deck)
    }

    private fun prefillValues(deck: Deck) {
        views.nameField.setText(deck.name)
        views.buttonBar.buttonOk.setText(R.string.button_save)
    }

    private fun saveWithValidation() {
        val name = views.nameField.text.toString()
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
            decksRegistry.addDeck(domain, name)
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

    companion object {
        fun create(context: Context, domain: Domain): Intent {
            return Intent(context, AddEditDeckActivity::class.java)
                    .putExtra(EXTRA_DOMAIN_ID, domain.id)
        }

        fun edit(context: Context, deck: Deck): Intent {
            return Intent(context, AddEditDeckActivity::class.java)
                    .putExtra(EXTRA_DOMAIN_ID, deck.domain.id)
                    .putExtra(EXTRA_DECK_ID, deck.id)
        }
    }
}