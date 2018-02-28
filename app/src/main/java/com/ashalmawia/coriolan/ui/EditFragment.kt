package com.ashalmawia.coriolan.ui

import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.data.LanguagesRegistry
import kotlinx.android.synthetic.main.edit.*

class EditFragment : Fragment() {

    private val items = listOf(
            EditListItem(R.string.edit__add_new_cards, { addNewCards(it) })
    )

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.edit, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeList()
    }

    private fun initializeList() {
        val adapter = EditListAdapter(items)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)
    }

    private fun addNewCards(context: Context) {
        val intent = AddEditCardActivity.create(context, LanguagesRegistry.original(), LanguagesRegistry.translations())
        context.startActivity(intent)
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