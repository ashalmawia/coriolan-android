package com.ashalmawia.coriolan.ui.commons.decks_list

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.ashalmawia.coriolan.R

abstract class BaseDeckListAdapter<DeckHolder : BaseDeckListViewHolder.DeckItem<DeckData>, DeckData>
    : RecyclerView.Adapter<BaseDeckListViewHolder>() {

    private val items = mutableListOf<BaseDeckListItem>()

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return item.type.ordinal
    }

    fun setItems(items: List<BaseDeckListItem>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseDeckListViewHolder {
        val context = parent.context
        val type = BaseDeckListItemType.values().find { it.ordinal == viewType }
                ?: throw IllegalStateException("unexpected view type value $viewType")
        return createViewHolder(context, parent, type)
    }

    override fun onBindViewHolder(holder: BaseDeckListViewHolder, position: Int) {
        val item = items[position]
        bindViewHolder(holder, item)
    }

    private fun createViewHolder(context: Context, parent: ViewGroup, type: BaseDeckListItemType): BaseDeckListViewHolder {
        return when (type) {
            BaseDeckListItemType.CATEGORY -> {
                val view = LayoutInflater.from(context).inflate(R.layout.edit_list_category_item, parent, false)
                BaseDeckListViewHolder.CategoryItem(view)
            }
            BaseDeckListItemType.DECK -> {
                createDeckViewHolder(context, parent)
            }
            BaseDeckListItemType.OPTION -> {
                val view = LayoutInflater.from(context).inflate(R.layout.edit_list_option_item, parent, false)
                BaseDeckListViewHolder.OptionItem(view)
            }
        }
    }

    private fun bindViewHolder(holder: BaseDeckListViewHolder, item: BaseDeckListItem) {
        return when (item.type) {
            BaseDeckListItemType.CATEGORY -> {
                (holder as BaseDeckListViewHolder.CategoryItem).bind(item as BaseDeckListItem.CategoryItem)
            }
            BaseDeckListItemType.DECK -> {
                @Suppress("UNCHECKED_CAST")
                (holder as DeckHolder).bind(item as BaseDeckListItem.DeckItem<DeckData>)
            }
            BaseDeckListItemType.OPTION -> {
                (holder as BaseDeckListViewHolder.OptionItem).bind(item as BaseDeckListItem.OptionItem)
            }
        }
    }

    protected abstract fun createDeckViewHolder(context: Context, parent: ViewGroup): DeckHolder
}