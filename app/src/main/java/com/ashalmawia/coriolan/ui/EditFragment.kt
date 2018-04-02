package com.ashalmawia.coriolan.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.DecksRegistry
import com.ashalmawia.coriolan.data.backup.ui.BackupActivity
import com.ashalmawia.coriolan.data.backup.ui.RestoreFromBackupActivity
import com.ashalmawia.coriolan.data.importer.DataImportCallback
import com.ashalmawia.coriolan.data.importer.DataImportFlow
import kotlinx.android.synthetic.main.edit.*

private const val REQUEST_CODE_UPDATE = 1

class EditFragment : Fragment() {

    private val items = listOf(
            EditListItem(R.string.edit__add_new_cards, { addNewCards(it) }),
            EditListItem(R.string.add_deck__title, { createNewDeck(it) }),
            EditListItem(R.string.import_from_file, { importFromFile(it) }),
            EditListItem(R.string.backup__create_title, { createBackup(it) }),
            EditListItem(R.string.backup__restore_title, { restoreFromBackup(it) })
    )

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

    private fun initializeList() {
        val adapter = EditListAdapter(items)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
    }

    private fun addNewCards(context: Context) {
        val domain = DecksRegistry.get().domain
        val intent = AddEditCardActivity.create(context, domain)
        startActivityForResult(intent, REQUEST_CODE_UPDATE)
    }

    private fun createNewDeck(context: Context) {
        val intent = AddEditDeckActivity.create(context)
        startActivityForResult(intent, REQUEST_CODE_UPDATE)
    }

    private fun importFromFile(context: Context) {
        DataImportFlow.start(context, DataImportFlow.default(), object : DataImportCallback {
            override fun onSuccess() {
                Toast.makeText(context, R.string.import_success, Toast.LENGTH_SHORT).show()
                notifyDataUpdated()
            }

            override fun onError(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createBackup(context: Context) {
        val intent = BackupActivity.intent(context)
        context.startActivity(intent)
    }

    private fun restoreFromBackup(context: Context) {
        val intent = RestoreFromBackupActivity.intent(context)
        startActivityForResult(intent, REQUEST_CODE_UPDATE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_UPDATE -> {
                if (resultCode == Activity.RESULT_OK) {
                    notifyDataUpdated()
                }
            }
        }
    }

    private fun notifyDataUpdated() {
        listener.onDataUpdated()
    }
}

private data class EditListItem(@StringRes val title: Int, val onClick: (Context) -> Unit)

private class EditListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title = itemView as TextView
}

private class EditListAdapter(private val options: List<EditListItem>) : RecyclerView.Adapter<EditListViewHolder>() {

    override fun getItemCount(): Int = options.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EditListViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return EditListViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditListViewHolder?, position: Int) {
        val item = options[position]
        holder!!.title.setText(item.title)
        holder.itemView.setOnClickListener { item.onClick.invoke(it.context) }
    }
}

interface EditFragmentListener {

    fun onDataUpdated()
}