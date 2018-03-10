package com.ashalmawia.coriolan.learning.scheduler

interface Scheduler {

    companion object {
        fun default(): Scheduler {
            return SpacedRepetitionScheduler()
        }
    }

    fun answers(state: State): Array<Answer>

    fun wrong(state: State): State

    fun hard(state: State): State

    fun correct(state: State): State

    fun easy(state: State): State
}

enum class Answer(private val value: Int) {
    UNKNOWN(-1),

    WRONG(0),
    HARD(1),
    CORRECT(2),
    EASY(3)
}