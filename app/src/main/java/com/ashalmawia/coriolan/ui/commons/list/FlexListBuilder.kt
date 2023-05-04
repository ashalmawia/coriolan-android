package com.ashalmawia.coriolan.ui.commons.list

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

class FlexListBuilder<Entity> {

    private val list = mutableListOf<FlexListItem>()

    fun addCategory(@StringRes title: Int): FlexListBuilder<Entity> {
        list.add(FlexListItem.CategoryItem(title))
        return this
    }

    fun addEntities(entities: List<Entity>): FlexListBuilder<Entity> {
        list.addAll(entities.map { FlexListItem.EntityItem(it) })
        return this
    }

    fun addOption(@StringRes title: Int, onClick: (Context) -> Unit, @DrawableRes icon: Int? = null): FlexListBuilder<Entity> {
        list.add(FlexListItem.OptionItem(title, icon, onClick))
        return this
    }

    fun build(): List<FlexListItem> = list
}