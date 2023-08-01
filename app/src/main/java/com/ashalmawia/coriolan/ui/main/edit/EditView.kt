package com.ashalmawia.coriolan.ui.main.edit

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.EditBinding
import com.ashalmawia.coriolan.model.Deck
import com.ashalmawia.coriolan.model.Domain
import com.ashalmawia.coriolan.ui.BaseActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditCardActivity
import com.ashalmawia.coriolan.ui.add_edit.AddEditDeckActivity
import com.ashalmawia.coriolan.ui.commons.list.FlexListBuilder
import com.ashalmawia.coriolan.ui.commons.list.FlexListItem
import com.ashalmawia.coriolan.ui.overview.OverviewActivity

interface EditView {
    fun onDecksList(domain: Domain, decks: List<EditDeckListItem>)
    fun showDeleteFailedDialog(deck: Deck)
}

class EditViewImpl(
        private val views: EditBinding,
        private val activity: BaseActivity,
        private val viewModel: EditViewModel
) : EditView, EditDeckCallback {

    init {
        val adapter = EditDeckListAdapter(this)
        views.list.adapter = adapter
        views.list.layoutManager = LinearLayoutManager(activity)
    }

    override fun onDecksList(domain: Domain, decks: List<EditDeckListItem>) {
        (views.list.adapter as EditDeckListAdapter).setItems(toFlexListItems(domain, decks))
    }

    private fun toFlexListItems(domain: Domain, decks: List<EditDeckListItem>): List<FlexListItem> {
        val builder = FlexListBuilder<EditDeckListItem>()

        builder.addCategory(R.string.decks__category_title)
        builder.addEntities(decks)
        builder.addOption(R.string.add_deck__title, { createNewDeck(domain) }, R.drawable.ic_add)

        return builder.build()
    }

    override fun onDeckClicked(deck: Deck) {
        val intent = OverviewActivity.intent(activity, deck)
        activity.startActivity(intent)
    }

    override fun addCards(deck: Deck) {
        val intent = AddEditCardActivity.add(activity, deck)
        activity.startActivity(intent)
    }

    override fun editDeck(deck: Deck) {
        val intent = AddEditDeckActivity.edit(activity, deck)
        activity.startActivity(intent)
    }

    override fun deleteDeck(deck: Deck) {
        val context = activity
        val dialog = AlertDialog.Builder(context)
                .setTitle(R.string.delete_deck__title)
                .setMessage(context.getString(R.string.delete_deck__message, deck.name))
                .setNegativeButton(R.string.button_cancel, null)
                .setPositiveButton(R.string.button_delete) { _, _ -> viewModel.deleteDeck(deck) }
        dialog.show()
    }

    override fun showDeleteFailedDialog(deck: Deck) {
        val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.delete_deck__title)
                .setMessage(activity.getString(R.string.delete_deck__failed, deck.name))
                .setNegativeButton(R.string.button_ok, null)
        dialog.show()
    }

    fun createNewDeck(domain: Domain) {
        val intent = AddEditDeckActivity.create(activity, domain)
        activity.startActivity(intent)
    }
}