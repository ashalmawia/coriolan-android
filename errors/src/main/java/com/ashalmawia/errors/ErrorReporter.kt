package com.ashalmawia.errors

interface ErrorReporter {

    fun report(tag: String, error: Throwable)
}

internal class StrictErrorReporter : ErrorReporter {

    override fun report(tag: String, error: Throwable) {
        throw error
    }
}

internal class SoftErrorReporter(private val loggers: List<Logger>) : ErrorReporter {

    override fun report(tag: String, error: Throwable) {
        loggers.forEach { it.logError(tag, error) }
    }
}