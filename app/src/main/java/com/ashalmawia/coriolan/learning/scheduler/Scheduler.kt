package com.ashalmawia.coriolan.learning.scheduler

import com.ashalmawia.coriolan.learning.LearningAnswer
import com.ashalmawia.coriolan.learning.scheduler.sr.SRState

interface Scheduler {

    fun answers(state: SRState): Array<LearningAnswer>

    fun processAnswer(answer: LearningAnswer, state: SRState): SRState
}