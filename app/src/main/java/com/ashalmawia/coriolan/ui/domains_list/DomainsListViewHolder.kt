package com.ashalmawia.coriolan.ui.domains_list

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.ashalmawia.coriolan.R
import com.ashalmawia.coriolan.databinding.DomainListItemBinding
import com.ashalmawia.coriolan.databinding.DomainListOptionItemBinding
import com.ashalmawia.coriolan.databinding.DomainsListCategoryItemBinding
import com.ashalmawia.coriolan.util.setStartDrawableTint

sealed class DomainsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class Category(private val views: DomainsListCategoryItemBinding): DomainsListViewHolder(views.root) {

        fun bind(item: DomainsListItem.CategoryItem) {
            views.root.setText(item.title)
        }
    }

    class Domain(private val views: DomainListItemBinding): DomainsListViewHolder(views.root) {

        fun bind(item: DomainsListItem.DomainItem) {
            views.apply {
                name.text = item.domain.name
                root.setOnClickListener { item.onClick(it.context) }
                more.setOnClickListener { item.onMoreClick(more) }
            }
        }
    }

    class Option(private val views: DomainListOptionItemBinding): DomainsListViewHolder(views.root) {
        
        fun bind(item: DomainsListItem.OptionItem) {
            val title = views.root
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