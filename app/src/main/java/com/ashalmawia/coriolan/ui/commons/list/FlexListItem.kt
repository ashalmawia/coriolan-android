package com.ashalmawia.coriolan.ui.commons.list

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class FlexListItem {
    abstract val type: FlexListItemType

    data class CategoryItem(@StringRes val title: Int) : FlexListItem() {
        override val type: FlexListItemType
            get() = FlexListItemType.CATEGORY
    }

    data class EntityItem<Entity>(val entity: Entity) : FlexListItem() {
        override val type: FlexListItemType
            get() = FlexListItemType.ENTITY
    }

    data class OptionItem(@StringRes val title: Int, @DrawableRes val icon: Int?, val onClick: (Context) -> Unit) : FlexListItem() {
        override val type: FlexListItemType
            get() = FlexListItemType.OPTION
    }
}