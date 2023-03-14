package com.ashalmawia.coriolan.ui.domains_list

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.util.setStartDrawableTint
import kotlinx.android.synthetic.main.domain_list_item.view.*

sealed class DomainsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class Category(itemView: View): DomainsListViewHolder(itemView) {
        val title = itemView as TextView

        fun bind(item: DomainsListItem.CategoryItem) {
            title.setText(item.title)
        }
    }

    class Domain(itemView: View): DomainsListViewHolder(itemView) {
        fun bind(item: DomainsListItem.DomainItem) {
            itemView.name.text = item.domain.name
            itemView.setOnClickListener { item.onClick(it.context) }
        }
    }

    class Option(itemView: View): DomainsListViewHolder(itemView) {
        val title = itemView as TextView
        
        fun bind(item: DomainsListItem.OptionItem) {
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