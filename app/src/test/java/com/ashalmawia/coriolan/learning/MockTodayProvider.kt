package com.ashalmawia.coriolan.learning

class MockTodayProvider : TodayProvider {

    private val today = mockToday()

    override fun today() = today

    override fun dayChanged() {
    }

    override fun register(listener: TodayChangeListener) {
    }

    override fun unregister(listener: TodayChangeListener) {
    }
}