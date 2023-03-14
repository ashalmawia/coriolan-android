package com.ashalmawia.coriolan.ui.domains_list

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup

class DomainsListAdapter(private val items: List<DomainsListItem>) : RecyclerView.Adapter<DomainsListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DomainsListViewHolder {
        return DomainsListItemType.values().find { it.ordinal == viewType }
                ?.createViewHolder(parent.context, parent)
                ?: throw IllegalStateException("unknown view type $viewType")
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].type.ordinal

    override fun onBindViewHolder(holder: DomainsListViewHolder, position: Int) {
        val item = items[position]
        item.type.bindViewHolder(holder, item)
    }
}