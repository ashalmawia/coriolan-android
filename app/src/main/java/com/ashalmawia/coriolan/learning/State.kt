package com.ashalmawia.coriolan.learning

interface State {

    val status: Status
}

enum class StateType {
    UNKNOWN,
    SR_STATE
}