package com.ashalmawia.coriolan.learning.scheduler

import java.util.*

interface Scheduler {

    companion object {
        fun default(): Scheduler {
            return SpacedRepetitionScheduler()
        }
    }

    fun wrong(state: State): State

    fun correct(state: State): State
}

fun today(): Date {
    return Date()
}