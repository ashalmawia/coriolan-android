package com.ashalmawia.coriolan.learning.scheduler

interface Scheduler {

    companion object {
        fun default(): Scheduler {
            return SpacedRepetitionScheduler()
        }
    }

    fun wrong(state: State): State

    fun correct(state: State): State
}