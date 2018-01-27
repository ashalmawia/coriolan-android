package com.ashalmawia.errors

class MockLogger : Logger {

    var lastTag: String? = null
    var lastError: Throwable? = null
    var timesCalled = 0

    override fun logError(tag: String, error: Throwable) {
        lastTag = tag
        lastError = error
        timesCalled++
    }
}

class MockReporter : ErrorReporter {

    var lastTag: String? = null
    var lastError: Throwable? = null
    var timesCalled = 0

    override fun report(tag: String, error: Throwable) {
        lastTag = tag
        lastError = error
        timesCalled++
    }
}

class MockStacktraceCutter : StacktraceCutter {

    override fun cut(throwable: Throwable): Throwable {
        return throwable
    }
}