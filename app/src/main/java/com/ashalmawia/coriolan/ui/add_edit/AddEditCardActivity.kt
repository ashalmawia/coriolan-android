package com.ashalmawia.coriolan.ui.add_edit

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.AddCardResult
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.model.*
import com.ashalmawia.coriolan.ui.BaseActivity
import kotlinx.android.synthetic.main.add_edit_card.*
import org.koin.android.ext.android.get

private const val EXTRA_DECK_ID = "deck_id"
private const val EXTRA_CARD_ID = "card_id"

private const val KEY_ORIGINAL = "original"
private const val KEY_TRANSLATIONS = "translations"
private const val KEY_DECK_SELECTION_POSITION = "deck_id"

class AddEditCardActivity : BaseActivity() {

    private val repository: Repository = get()
    private val decksRegistry: DecksRegistry = domainScope().get()
    private val domain: Domain = domainScope().get()

    private var card: Card? = null
    private var extras: Extras? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_card)

        setUpToolbar(R.string.edit__add_new_cards)

        extractData()
        initialize()
        prefill()
    }

    private val isInEditMode
        get() = card != null

    private fun extractData() {
        if (intent.hasExtra(EXTRA_CARD_ID)) {
            extractDataEditCard()
        }
    }

    private fun extractDataEditCard() {
        val cardId = intent.getLongExtra(EXTRA_CARD_ID, -1)
        val card = repository.cardById(cardId, domain) ?: throw IllegalStateException("card with id[$cardId] does not exist in the database")

        if (card.translations.isEmpty()) throw IllegalStateException("card with id[$cardId] has no translations")

        this.card = card
        this.extras = card.original.extras
    }

    private fun initialize() {
        labelOriginal.text = getString(R.string.edit_card__original, domain.langOriginal().value)
        labelTranslations.text = getString(R.string.edit_card__translations, domain.langTranslations().value)

        findViewById<AddEditCardItemView>(R.id.original).canBeDeleted = false
        findViewById<AddEditCardItemView>(R.id.transcription).canBeDeleted = false

        deckSelector.initialize(decks())

        addTranslation.setOnClickListener { onAddNewTranslationClicked() }
        mockInputField.setOnClickListener { onAddNewTranslationClicked() }
    }

    private fun prefill() {
        if (isInEditMode) {
            prefillDataEdit(card!!)
        } else {
            prefillDataAdd()
            addTrasnlationField()
        }
    }

    private fun prefillDataAdd() {
        val deckId = intent.getLongExtra(EXTRA_DECK_ID, -1L)
        if (deckId == -1L) {
            return
        }

        deckSelector.selectDeckWithId(deckId)
    }

    private fun prefillDataEdit(card: Card) {
        deckSelector.selectDeckWithId(card.deckId)

        original.input = card.original.value

        transcription.input = extras?.transcription ?: ""

        card.translations.forEach {
            val view = addTrasnlationField()
            view.input = it.value
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.add_edit_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu__done -> {
                onSaveClicked()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onAddNewTranslationClicked() {
        val view = addTrasnlationField()
        view.showKeyboard()
    }

    private fun addTrasnlationField(): AddEditCardItemView {
        val view = AddEditCardItemView(this)
        view.removeListener = { onRemoveClicked(it) }

        translationsContainer.addView(view)
        updateTranslationViews()

        return view
    }

    private fun onRemoveClicked(view: AddEditCardItemView) {
        translationsContainer.removeView(view)
        updateTranslationViews()
    }

    private fun updateTranslationViews() {
        val translationsCount = translationsContainer.childCount
        for (i in 0 until translationsCount) {
            val child = translationsContainer.getChildAt(i) as AddEditCardItemView
            child.ordinal = i + 1
            child.canBeDeleted = translationsCount > 1
        }
    }

    private fun decks(): List<Deck> {
        return repository.allDecks(domain)
    }

    private fun onSaveClicked() {
        val data = collectCardData()

        if (!validate(data)) {
            return
        }

        if (isInEditMode) {
            save(data)
        } else {
            add(data)
        }
    }

    private fun add(data: CardData) {
        val result = decksRegistry.addCardToDeck(data)

        when (result) {
            AddCardResult.Success -> {
                confirm()
                clear()
            }
            is AddCardResult.Duplicate -> {
                notifyDuplicate(result.card)
            }
        }
    }

    private fun save(data: CardData) {
        decksRegistry.editCard(card!!, data)

        confirm()

        finishOk()
    }

    private fun collectCardData(): CardData {
        val original = original.input
        val transcription = transcription.input.takeIf { it.isNotBlank() }
        val translations = collectTranslations()

        val deck = deckSelector.selectedDeck()

        return CardData(
                original,
                transcription,
                translations.asList(),
                deck.id
        )
    }

    private fun collectTranslations(): Array<String> {
        val array = Array(translationsContainer.childCount) { "" }
        for (i in 0 until array.size) {
            val view = translationsContainer.getChildAt(i) as AddEditCardItemView
            array[i] = view.input
        }
        return array
    }

    private fun confirm() {
        val messageId = if (isInEditMode) R.string.edit_card__saved else R.string.edit_card__added
        Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show()
    }

    private fun clear() {
        original.input = ""
        transcription.input = ""
        translationsContainer.removeAllViews()
        addTrasnlationField()
    }

    private fun validate(cardData: CardData): Boolean {
        val onError = this::showError

        if (!CardValidator.validateOriginalNotEmpty(cardData.original, onError)) {
            return false
        }

        if (!CardValidator.validateHasTranslations(cardData.translations, onError)) {
            return false
        }

        if (!CardValidator.validateNoDuplicates(cardData.translations, onError)) {
            return false
        }

        return true
    }

    private fun notifyDuplicate(duplicate: Card) {
        val deck = repository.deckById(duplicate.deckId, duplicate.domain)!!
        val message = resources.getString(R.string.add_card__duplicate, deck.name)
        showError(message)
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_DECK_SELECTION_POSITION, deckSelector.selectedItemPosition)
        outState.putString(KEY_ORIGINAL, original.input)
        val translations = collectTranslations()
        outState.putStringArray(KEY_TRANSLATIONS, translations)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        deckSelector.setSelection(savedInstanceState.getInt(KEY_DECK_SELECTION_POSITION, 0), false)
        original.input = savedInstanceState.getString(KEY_ORIGINAL, "")

        val translations = savedInstanceState.getStringArray(KEY_TRANSLATIONS)
        translationsContainer.removeAllViews()
        translations?.forEach {
            val view = addTrasnlationField()
            view.input = it
        }
    }

    override fun onBackPressed() {
        if (anyDataChanged()) {
            showExitConfirmationDialog()
        } else {
            navigateBack()
        }
    }

    private var shownDialog: Dialog? = null

    private fun showExitConfirmationDialog() {
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.add_edit_card__confirm_exist__title)
                .setMessage(R.string.add_edit_card__confirm_exist__message)
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_leave) { _, _ -> navigateBack() }
                .create()
        dialog.show()

        shownDialog = dialog
    }

    private fun anyDataChanged(): Boolean {
        val data = collectCardData()

        return if (isInEditMode) {
            !data.matches(card!!)
        } else {
            data.isNotEmpty()
        }
    }

    private fun CardData.matches(card: Card): Boolean {
        return original == card.original.value
                && transcription == extras?.transcription
                && card.translations.size == translations.size
                && card.translations.map { it.value }.containsAll(translations)
    }

    private fun CardData.isNotEmpty(): Boolean {
        return original.isNotBlank()
                || !transcription.isNullOrBlank()
                || translations.any { it.isNotBlank() }
    }

    override fun onStop() {
        super.onStop()
        shownDialog?.cancel()
    }

    companion object {
        fun add(context: Context, deck: Deck): Intent {
            val intent = Intent(context, AddEditCardActivity::class.java)
            intent.putExtra(EXTRA_DECK_ID, deck.id)
            return intent
        }

        fun edit(context: Context, card: Card): Intent {
            val intent = Intent(context, AddEditCardActivity::class.java)
            intent.putExtra(EXTRA_CARD_ID, card.id)
            return intent
        }
    }
}

object CardValidator {

    fun validateDeckSelected(ordinal: Int, decks: List<Deck>, onError: (String) -> Unit): Boolean
        = validate(ordinal >= 0 && ordinal < decks.size, "Please select a deck", onError)

    fun validateOriginalNotEmpty(original: String, onError: (String) -> Unit): Boolean
        = validate(!original.isBlank(), "Please enter the original", onError)

    fun validateHasTranslations(translations: List<String>, onError: (String) -> Unit): Boolean
        = validate(translations.filterNot { it.isBlank() }.isNotEmpty(), "Please enter at least one translation", onError)

    fun validateNoDuplicates(translations: List<String>, onError: (String) -> Unit): Boolean {
        val nonEmpty = translations.filterNot { it.isBlank() }
        return validate(nonEmpty.size == nonEmpty.distinct().size, "Some of translations duplicate each other", onError)
    }

    private fun validate(condition: Boolean, error: String, onError: (String) -> Unit): Boolean {
        if (!condition) {
            onError(error)
        }
        return condition
    }
}