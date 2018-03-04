package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.CardData
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.model.Card
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.ExpressionType
import com.ashalmawia.coriolan.model.Language
import com.ashalmawia.coriolan.util.setUpToolbar
import com.ashalmawia.errors.Errors
import kotlinx.android.synthetic.main.add_edit_card.*

private const val EXTRA_LANG_ORIGINAL = "lang_original"
private const val EXTRA_LANG_TRANSLATIONS = "lang_translation"
private const val EXTRA_CARD_ID = "card_id"

private const val KEY_ORIGINAL = "original"
private const val KEY_TRANSLATIONS = "translations"
private const val KEY_DECK_SELECTION_POSITION = "deck_id"

class AddEditCardActivity : AppCompatActivity() {

    private val repository = Repository.get(this)

    private lateinit var originalLang: Language
    private lateinit var translationsLang: Language

    private var card: Card? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_card)

        setUpToolbar(R.string.edit__add_new_cards)

        extractData()

        initialize()
    }

    private val isInEditMode
        get() = card != null

    private fun extractData() {
        if (intent.hasExtra(EXTRA_CARD_ID)) {
            extractDataEditCard()
        } else {
            extractDataNewCard()
        }
    }

    private fun extractDataEditCard() {
        val cardId = intent.getLongExtra(EXTRA_CARD_ID, -1)
        val card = repository.cardById(cardId) ?: throw IllegalStateException("card with id[$cardId] does not exist in the database")
        originalLang = card.original.language

        if (card.translations.isEmpty()) throw IllegalStateException("card with id[$cardId] has no translations")
        translationsLang = card.translations[0].language

        this.card = card
    }

    private fun extractDataNewCard() {
        val originalLang = repository.languageById(intent.getLongExtra(EXTRA_LANG_ORIGINAL, -1))
        val translationsLang = repository.languageById(intent.getLongExtra(EXTRA_LANG_TRANSLATIONS, -1))
        if (originalLang == null || translationsLang == null) {
            throw IllegalStateException("activity was not properly initialized")
        }

        this.originalLang = originalLang
        this.translationsLang = translationsLang
    }

    private fun initialize() {
        labelOriginal.text = getString(R.string.edit_card__original, originalLang.value)
        labelTranslations.text = getString(R.string.edit_card__translations, translationsLang.value)

        findViewById<AddEditCardItemView>(R.id.original).canBeDeleted = false
        original.removeListener = { onRemoveClicked(it) }

        buttonCancel.setOnClickListener { finish() }

        buttonOk.setOnClickListener { onSaveClicked() }
        buttonOk.setText(if (isInEditMode) R.string.button_save else R.string.button_add)

        initializeDecksDropDown()

        addTranslation.setOnClickListener { onAddNewTranslationClicked() }
        mockInputField.setOnClickListener { onAddNewTranslationClicked() }

        if (isInEditMode) {
            prefillData(card!!)
        } else {
            addTrasnlationField()
        }
    }

    private fun prefillData(card: Card) {
        val position = (deckSelector.adapter as DecksSelectorAdapter).positionOfDeck(card.deckId)
        deckSelector.setSelection(position, false)

        original.input = card.original.value

        card.translations.forEach {
            val view = addTrasnlationField()
            view.input = it.value
        }
    }

    private fun onAddNewTranslationClicked() {
        val view = addTrasnlationField()
        view.showKeyboard()
    }

    private fun addTrasnlationField(): AddEditCardItemView {
        val count = translationsContainer.childCount + 1

        val view = AddEditCardItemView(this)
        view.canBeDeleted = count > 1       // the first translation can't be deleted
        view.ordinal = count
        view.removeListener = { onRemoveClicked(it) }

        translationsContainer.addView(view)

        return view
    }

    private fun onRemoveClicked(view: AddEditCardItemView) {
        translationsContainer.removeView(view)
        resetTranslationsOrdinals()
    }

    private fun resetTranslationsOrdinals() {
        for (i in 0 until translationsContainer.childCount) {
            val child = translationsContainer.getChildAt(i) as AddEditCardItemView
            child.ordinal = i + 1
        }
    }

    private fun initializeDecksDropDown() {
        deckSelector.adapter = DecksSelectorAdapter(this, decks())
        deckSelector.setSelection(0, false)
    }

    private fun decks(): List<Deck> {
        return repository.allDecks()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onSaveClicked() {
        val data = collectCardDataWithValidation() ?: return

        if (isInEditMode) {
            save(data)
        } else {
            add(data)
        }
    }

    private fun add(data: CardData) {
        DecksRegistry.get().addCardToDeck(data)

        confirm()
        clear()
    }

    private fun save(data: CardData) {
        DecksRegistry.get().editCard(card!!, data)

        confirm()

        finishOk()
    }

    private fun finishOk() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun collectCardDataWithValidation(): CardData? {
        val deckPosition = deckSelector.selectedItemPosition
        val original = original.input
        val translations = collectTranslations()

        if (!validate(deckPosition, original, translations)) {
            return null
        }

        val deck = deckSelector.selectedItem as Deck

        return CardData(
                original,
                originalLang,
                translations.asList(),
                translationsLang,
                deck.id,
                ExpressionType.WORD
        )
    }

    private fun collectTranslations(): Array<String> {
        val array = Array(translationsContainer.childCount, { _ -> "" })
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
        translationsContainer.removeAllViews()
        addTrasnlationField()
    }

    private fun validate(decksPosition: Int, original: String, translations: Array<String>): Boolean {
        val onError = this::showError

        if (!CardValidator.validateDeckSelected(decksPosition, decks(), onError)) {
            return false
        }

        if (!CardValidator.validateOriginalNotEmpty(original, onError)) {
            return false
        }

        if (!CardValidator.validateHasTranslations(translations, onError)) {
            return false
        }

        if (!CardValidator.validateNoDuplicates(translations, onError)) {
            return false
        }

        return true
    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt(KEY_DECK_SELECTION_POSITION, deckSelector.selectedItemPosition)
        outState.putString(KEY_ORIGINAL, original.input)
        val translations = collectTranslations()
        outState.putStringArray(KEY_TRANSLATIONS, translations)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        deckSelector.setSelection(savedInstanceState!!.getInt(KEY_DECK_SELECTION_POSITION, 0), false)
        original.input = savedInstanceState.getString(KEY_ORIGINAL, "")

        val translations = savedInstanceState.getStringArray(KEY_TRANSLATIONS)
        translationsContainer.removeAllViews()
        translations?.forEach {
            val view = addTrasnlationField()
            view.input = it
        }
    }

    companion object {
        fun create(context: Context, originalLang: Language, translationsLang: Language): Intent {
            val intent = Intent(context, AddEditCardActivity::class.java)
            intent.putExtra(EXTRA_LANG_ORIGINAL, originalLang.id)
            intent.putExtra(EXTRA_LANG_TRANSLATIONS, translationsLang.id)
            return intent
        }

        fun edit(context: Context, card: Card): Intent {
            val intent = Intent(context, AddEditCardActivity::class.java)
            intent.putExtra(EXTRA_CARD_ID, card.id)
            return intent
        }
    }
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
        return decks[position].id
    }

    override fun getCount(): Int {
        return decks.size
    }

    fun positionOfDeck(deckId: Long): Int {
        val index = decks.indexOfFirst { it.id == deckId }
        return if (index == -1) {
            Errors.illegalState(TAG, "deck with id[$deckId] not in the adapter, falling back to default")
            0
        } else {
            index
        }
    }
}

object CardValidator {

    fun validateDeckSelected(ordinal: Int, decks: List<Deck>, onError: (String) -> Unit): Boolean
        = validate(ordinal >= 0 && ordinal < decks.size, "Please select a deck", onError)

    fun validateOriginalNotEmpty(original: String, onError: (String) -> Unit): Boolean
        = validate(!TextUtils.isEmpty(original), "Please enter the original", onError)

    fun validateHasTranslations(translations: Array<String>, onError: (String) -> Unit): Boolean
        = validate(translations.filterNot { TextUtils.isEmpty(it) }.isNotEmpty(), "Please enter at least one translation", onError)

    fun validateNoDuplicates(translations: Array<String>, onError: (String) -> Unit): Boolean {
        val nonEmpty = translations.filterNot { TextUtils.isEmpty(it) }
        return validate(nonEmpty.size == nonEmpty.distinct().size, "Some of translations duplicate each other", onError)
    }

    private fun validate(condition: Boolean, error: String, onError: (String) -> Unit): Boolean {
        if (!condition) {
            onError.invoke(error)
        }
        return condition
    }
}