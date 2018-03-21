package com.ashalmawia.coriolan.learning.scheduler

interface State {

    val status: Status
}

enum class StateType {
    UNKNOWN,
    SR_STATE
}