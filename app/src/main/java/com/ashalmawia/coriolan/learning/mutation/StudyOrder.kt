package com.ashalmawia.coriolan.learning.mutation

enum class StudyOrder {
    ORDER_ADDED,
    RANDOM,
    NEWEST_FIRST;

    companion object {
        // todo: move default study order to settings
        fun default() = RANDOM
    }
}