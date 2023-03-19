package com.ashalmawia.coriolan.learning

import com.ashalmawia.coriolan.learning.exercise.sr.SRState

data class State(
    val spacedRepetition: SRState
)

interface ExerciseState {
    val status: Status
}

enum class StateType {
    UNKNOWN,
    SR_STATE
}