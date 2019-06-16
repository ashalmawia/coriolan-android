package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ashalmawia.coriolan.R

enum class DomainsListItemType {
    CATEGORY {
        override fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.domains_list_category_item, parent, false)
            return DomainsListViewHolder.Category(view)
        }

        override fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem) {
            (holder as DomainsListViewHolder.Category).bind(item as DomainsListItem.CategoryItem)
        }
    },
    DOMAIN {
        override fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.domain_list_item, parent, false)
            return DomainsListViewHolder.Domain(view)
        }

        override fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem) {
            (holder as DomainsListViewHolder.Domain).bind(item as DomainsListItem.DomainItem)
        }
    },
    OPTION {
        override fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.domain_list_option_item, parent, false)
            return DomainsListViewHolder.Option(view)
        }

        override fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem) {
            (holder as DomainsListViewHolder.Option).bind(item as DomainsListItem.OptionItem)
        }
    };

    abstract fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder
    abstract fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem)
}