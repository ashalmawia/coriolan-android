package com.ashalmawia.coriolan.ui.domains_list

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ashalmawia.coriolan.model.Domain

sealed class DomainsListItem {
    abstract val type: DomainsListItemType

    data class CategoryItem(@StringRes val title: Int) : DomainsListItem() {
        override val type: DomainsListItemType
            get() = DomainsListItemType.CATEGORY
    }

    data class DomainItem(val domain: Domain, val onClick: (Context) -> Unit) : DomainsListItem() {
        override val type
            get() = DomainsListItemType.DOMAIN
    }

    data class OptionItem(
            @StringRes val title: Int,
            @DrawableRes val icon: Int?,
            val onClick: (Context) -> Unit
    ) : DomainsListItem() {

        override val type
            get() = DomainsListItemType.OPTION
    }
}