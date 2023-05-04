package com.ashalmawia.coriolan.ui.commons.list

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.ashalmawia.coriolan.R

abstract class FlexListAdapter<EntityHolder : FlexListViewHolder.EntityItem<Entity>, Entity>
    : RecyclerView.Adapter<FlexListViewHolder>() {

    private val items = mutableListOf<FlexListItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return item.type.ordinal
    }

    fun setItems(items: List<FlexListItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlexListViewHolder {
        val context = parent.context
        val type = FlexListItemType.values().find { it.ordinal == viewType }
                ?: throw IllegalStateException("unexpected view type value $viewType")
        return createViewHolder(context, parent, type)
    }

    override fun onBindViewHolder(holder: FlexListViewHolder, position: Int) {
        val item = items[position]
        bindViewHolder(holder, item)
    }

    private fun createViewHolder(context: Context, parent: ViewGroup, type: FlexListItemType): FlexListViewHolder {
        return when (type) {
            FlexListItemType.CATEGORY -> {
                val view = LayoutInflater.from(context).inflate(R.layout.edit_list_category_item, parent, false)
                FlexListViewHolder.CategoryItem(view)
            }
            FlexListItemType.ENTITY -> {
                createEntityViewHolder(context, parent)
            }
            FlexListItemType.OPTION -> {
                val view = LayoutInflater.from(context).inflate(R.layout.edit_list_option_item, parent, false)
                FlexListViewHolder.OptionItem(view)
            }
        }
    }

    private fun bindViewHolder(holder: FlexListViewHolder, item: FlexListItem) {
        return when (item.type) {
            FlexListItemType.CATEGORY -> {
                (holder as FlexListViewHolder.CategoryItem).bind(item as FlexListItem.CategoryItem)
            }
            FlexListItemType.ENTITY -> {
                @Suppress("UNCHECKED_CAST")
                (holder as EntityHolder).bind(item as FlexListItem.EntityItem<Entity>)
            }
            FlexListItemType.OPTION -> {
                (holder as FlexListViewHolder.OptionItem).bind(item as FlexListItem.OptionItem)
            }
        }
    }

    protected abstract fun createEntityViewHolder(context: Context, parent: ViewGroup): EntityHolder
}