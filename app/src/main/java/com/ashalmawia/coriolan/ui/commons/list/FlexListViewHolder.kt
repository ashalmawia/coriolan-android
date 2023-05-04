package com.ashalmawia.coriolan.ui.commons.list

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.util.setStartDrawableTint

sealed class FlexListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class CategoryItem(itemView: View): FlexListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: FlexListItem.CategoryItem) {
            title.setText(item.title)
        }
    }

    abstract class EntityItem<Entity>(itemView: View): FlexListViewHolder(itemView) {
        abstract fun bind(item: FlexListItem.EntityItem<Entity>)
    }

    class OptionItem(itemView: View): FlexListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: FlexListItem.OptionItem) {
            title.setText(item.title)
            if (item.icon != null) {
                title.setCompoundDrawablesRelativeWithIntrinsicBounds(item.icon, 0, 0, 0)
                title.setStartDrawableTint(R.color.colorPrimary)
            } else {
                title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
            }
            itemView.setOnClickListener { item.onClick(it.context) }
        }
    }
}