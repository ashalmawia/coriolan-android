package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import android.view.ViewGroup
import com.ashalmawia.coriolan.databinding.DomainListItemBinding
import com.ashalmawia.coriolan.databinding.DomainListOptionItemBinding
import com.ashalmawia.coriolan.databinding.DomainsListCategoryItemBinding
import com.ashalmawia.coriolan.ui.util.layoutInflater

enum class DomainsListItemType {
    CATEGORY {
        override fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder {
            return DomainsListViewHolder.Category(
                    DomainsListCategoryItemBinding.inflate(context.layoutInflater, parent, false)
            )
        }

        override fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem) {
            (holder as DomainsListViewHolder.Category).bind(item as DomainsListItem.CategoryItem)
        }
    },
    DOMAIN {
        override fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder {
            return DomainsListViewHolder.Domain(
                    DomainListItemBinding.inflate(context.layoutInflater, parent, false)
            )
        }

        override fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem) {
            (holder as DomainsListViewHolder.Domain).bind(item as DomainsListItem.DomainItem)
        }
    },
    OPTION {
        override fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder {
            return DomainsListViewHolder.Option(
                    DomainListOptionItemBinding.inflate(context.layoutInflater, parent, false)
            )
        }

        override fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem) {
            (holder as DomainsListViewHolder.Option).bind(item as DomainsListItem.OptionItem)
        }
    };

    abstract fun createViewHolder(context: Context, parent: ViewGroup): DomainsListViewHolder
    abstract fun bindViewHolder(holder: DomainsListViewHolder, item: DomainsListItem)
}