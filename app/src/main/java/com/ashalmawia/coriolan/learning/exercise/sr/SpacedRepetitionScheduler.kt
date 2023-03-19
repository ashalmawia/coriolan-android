package com.ashalmawia.coriolan.learning.exercise.sr

interface SpacedRepetitionScheduler {

    fun processAnswer(answer: SRAnswer, state: SRState): SRState
}