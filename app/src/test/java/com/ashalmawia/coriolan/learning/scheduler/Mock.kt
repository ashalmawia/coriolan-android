package com.ashalmawia.coriolan.learning.scheduler

class MockScheduler : Scheduler {

    override fun wrong(state: State): State {
        return state
    }

    override fun correct(state: State): State {
        return state
    }
}