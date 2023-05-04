package com.ashalmawia.coriolan.ui.overview

import androidx.annotation.StringRes
import com.ashalmawia.coriolan.R

enum class OverviewSorting(@StringRes val titleRes: Int) {
    DATE_ADDED_NEWEST_FIRST(R.string.overview__sorting_date_added_acs),
    DATE_ADDED_OLDEST_FIRST(R.string.overview__sorting_date_added_des),
    ALPHABETICALLY_A_Z(R.string.overview__sorting_name_acs),
    ALPHABETICALLY_Z_A(R.string.overview__sorting_name_des);

    companion object {
        fun default() = DATE_ADDED_NEWEST_FIRST
    }
}