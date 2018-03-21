package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.scheduler.sr.SRState

interface Scheduler {

    fun answers(state: SRState): Array<Answer>

    fun wrong(state: SRState): SRState

    fun hard(state: SRState): SRState

    fun correct(state: SRState): SRState

    fun easy(state: SRState): SRState
}

enum class Answer(private val value: Int) {
    UNKNOWN(-1),

    WRONG(0),
    HARD(1),
    CORRECT(2),
    EASY(3)
}