package com.ashalmawia.coriolan.learning.exercise.sr

import com.ashalmawia.coriolan.learning.SRAnswer
import com.ashalmawia.coriolan.learning.exercise.sr.SRState

interface Scheduler {

    fun answers(state: SRState): Array<SRAnswer>

    fun processAnswer(answer: SRAnswer, state: SRState): SRState
}