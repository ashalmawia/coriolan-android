package com.ashalmawia.coriolan.ui.main.edit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.storage.Repository
import com.ashalmawia.coriolan.databinding.EditBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseFragment
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditDeckActivity
import com.ashalmawia.coriolan.ui.commons.decks_list.BaseDeckListItem
import org.koin.android.ext.android.inject

private const val ARGUMENT_DOMAIN_ID = "domain_id"

class EditFragment : BaseFragment(), EditDeckCallback {

    companion object {
        fun create(domain: Domain): EditFragment {
            val arguments = Bundle().also {
                it.putLong(ARGUMENT_DOMAIN_ID, domain.id)
            }
            return EditFragment().also { it.arguments = arguments }
        }
    }

    private lateinit var views: EditBinding

    private val repository: Repository by inject()
    private val domain: Domain by lazy {
        val domainId = requireArguments().getLong(ARGUMENT_DOMAIN_ID)
        repository.domainById(domainId)!!
    }

    private lateinit var listener: EditFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as EditFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        views = EditBinding.inflate(inflater, container, false)
        return views.root
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
        val adapter = EditDeckListAdapter(this)
        views.list.adapter = adapter
        views.list.layoutManager = LinearLayoutManager(context)
        fetchData()
    }

    private fun fetchData() {
        (views.list.adapter as EditDeckListAdapter).setItems(items())
    }

    private fun items(): List<BaseDeckListItem> {
        val builder = EditListBuilder()

        builder.addCategory(R.string.decks__category_title)
        builder.addDecks(decks())
        builder.addOption(R.string.add_deck__title, { createNewDeck(it) }, R.drawable.ic_add)

//        builder.addCategory(R.string.import__category_title)
//        builder.addOption(R.string.import_from_file, { importFromFile() })

        return builder.build()
    }

    private fun decks(): List<Deck> {
        return repository.allDecks(domain)
    }

    override fun addCards(context: Context, deck: Deck) {
        val intent = AddEditCardActivity.add(context, deck)
        startActivity(intent)
    }

    override fun editDeck(context: Context, deck: Deck) {
        val intent = AddEditDeckActivity.edit(context, deck)
        context.startActivity(intent)
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
        val deleted = repository.deleteDeck(deck)
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

    private fun createNewDeck(context: Context) {
        val intent = AddEditDeckActivity.create(context, domain)
        startActivity(intent)
    }

//    private fun importFromFile() {
//        val flow = dataImportScope().get<DataImportFlow> { parametersOf(importerRegistry.default()) }
//        flow.callback = object : DataImportCallback {
//            override fun onSuccess() {
//                Toast.makeText(context, R.string.import_success, Toast.LENGTH_SHORT).show()
//                notifyDataUpdated()
//                dataImportScope().close()
//            }
//
//            override fun onError(message: String) {
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                dataImportScope().close()
//            }
//        }
//        flow.start()
//    }

//    private fun notifyDataUpdated() {
//        listener.onDataUpdated()
//    }
}

interface EditFragmentListener {

    fun onDataUpdated()
}