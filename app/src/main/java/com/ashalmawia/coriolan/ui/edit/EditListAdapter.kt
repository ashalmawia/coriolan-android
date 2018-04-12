package com.ashalmawia.coriolan.ui.edit

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class EditListAdapter : RecyclerView.Adapter<EditListViewHolder>() {

    private val items = mutableListOf<EditListItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return item.type.ordinal
    }

    fun setItems(items: List<EditListItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditListViewHolder {
        val context = parent.context
        return when (viewType) {
            EditListItemType.CATEGORY.ordinal -> EditListItemType.CATEGORY.createViewHolder(context, parent)
            EditListItemType.DECK.ordinal -> EditListItemType.DECK.createViewHolder(context, parent)
            EditListItemType.OPTION.ordinal -> EditListItemType.OPTION.createViewHolder(context, parent)

            else -> throw IllegalStateException("unexpected view type value $viewType")
        }
    }

    override fun onBindViewHolder(holder: EditListViewHolder, position: Int) {
        val item = items[position]
        item.type.bindViewHolder(holder, item)
    }
}