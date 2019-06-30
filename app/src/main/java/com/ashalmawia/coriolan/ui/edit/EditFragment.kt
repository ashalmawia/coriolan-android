package com.ashalmawia.coriolan.ui.edit

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.importer.DataImportCallback
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import com.ashalmawia.coriolan.data.importer.ImporterRegistry
import com.ashalmawia.coriolan.dependencies.dataImportScope
import com.ashalmawia.coriolan.dependencies.domainScope
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.DataFetcher
import kotlinx.android.synthetic.main.edit.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class EditFragment : BaseFragment(), EditDeckCallback, DataFetcher {

    private val importerRegistry: ImporterRegistry by inject()
    private val decksRegistry: DecksRegistry = domainScope().get()

    private lateinit var listener: EditFragmentListener

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as EditFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeList()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun initializeList() {
        val adapter = EditListAdapter()
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
        fetchData()
    }

    override fun fetchData() {
        (list.adapter as EditListAdapter).setItems(items())
    }

    private fun items(): List<EditListItem> {
        val builder = EditListBuilder()

        builder.addCategory(R.string.decks__category_title)
        builder.addDecks(decks(), this)
        builder.addOption(R.string.add_deck__title, { createNewDeck() }, R.drawable.ic_add)

        builder.addCategory(R.string.import__category_title)
        builder.addOption(R.string.import_from_file, { importFromFile() })

        builder.addCategory(R.string.backup__category_title)
        builder.addOption(R.string.backup__create_title, { createBackup() })
        builder.addOption(R.string.backup__restore_title, { restoreFromBackup() })

        return builder.build()
    }

    private fun decks(): List<Deck> {
        return decksRegistry.allDecks()
    }

    override fun addCards(deck: Deck) {
        navigator.openAddCardScreen(deck)
    }

    override fun editDeck(deck: Deck) {
        navigator.openEditDeckScreen(deck)
    }

    override fun deleteDeck(context: Context, deck: Deck) {
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.delete_deck__title)
                .setMessage(context.getString(R.string.delete_deck__message, deck.name))
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete) { _, _ -> performDeleteDeck(context, deck) }
        dialog.show()
    }

    private fun performDeleteDeck(context: Context, deck: Deck) {
        val deleted = decksRegistry.deleteDeck(deck)
        if (deleted) {
            fetchData()
        } else {
            showDeleteFailedDialog(context, deck)
        }
    }

    private fun showDeleteFailedDialog(context: Context, deck: Deck) {
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.delete_deck__title)
                .setMessage(context.getString(R.string.delete_deck__failed, deck.name))
                .setNegativeButton(R.string.button_ok, null)
        dialog.show()
    }

    private fun createNewDeck() {
        navigator.openAddDeckScreen()
    }

    private fun importFromFile() {
        val flow = dataImportScope().get<DataImportFlow> { parametersOf(importerRegistry.default()) }
        flow.callback = object : DataImportCallback {
            override fun onSuccess() {
                Toast.makeText(context, R.string.import_success, Toast.LENGTH_SHORT).show()
                notifyDataUpdated()
                dataImportScope().close()
            }

            override fun onError(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                dataImportScope().close()
            }
        }
        flow.start()
    }

    private fun createBackup() {
        navigator.openCreateBackupScreen()
    }

    private fun restoreFromBackup() {
        navigator.openRestoreFromBackupScreen()
    }

    private fun notifyDataUpdated() {
        listener.onDataUpdated()
    }
}

interface EditFragmentListener {

    fun onDataUpdated()
}