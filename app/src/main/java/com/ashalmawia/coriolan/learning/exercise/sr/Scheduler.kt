package com.ashalmawia.coriolan.learning.exercise.sr

interface Scheduler {

    fun answers(state: SRState): Array<SRAnswer>

    fun processAnswer(answer: SRAnswer, state: SRState): SRState
}