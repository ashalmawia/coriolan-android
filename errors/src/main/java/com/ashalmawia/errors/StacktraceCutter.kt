package com.ashalmawia.errors

interface StacktraceCutter {

    fun cut(throwable: Throwable): Throwable
}

object StacktraceCutterImpl : StacktraceCutter {

    override fun cut(throwable: Throwable): Throwable {
        val stacktrace = throwable.stackTrace.filterNot { it.className.startsWith("com.ashalmawia.errors.Errors") }
        throwable.stackTrace = stacktrace.toTypedArray()
        return throwable
    }
}