package com.ashalmawia.errors

import org.junit.Assert

fun addErrorsStacktraceElements(throwable: Throwable): Throwable {
    val stacktrace = throwable.stackTrace
    val processed = stacktrace.copyOf(stacktrace.size + 2)
    processed[stacktrace.size] = StackTraceElement(
            Errors::class.java.canonicalName,
            "illegalState",
            "Errors.kt",
            67
    )
    processed[stacktrace.size + 1] = StackTraceElement(
            Errors.Companion::class.java.canonicalName,
            "report",
            "ErrorReporter.kt",
            67
    )
    throwable.stackTrace = processed
    return throwable
}

fun addGeneralStacktraceElements(throwable: Throwable): Throwable {
    val stacktrace = throwable.stackTrace
    val processed = stacktrace.copyOf(stacktrace.size + 3)
    processed[stacktrace.size] = StackTraceElement(
            "com.ashalmawia.coriolan.MyClass",
            "someMethod",
            "MyClass.kt",
            22
    )
    processed[stacktrace.size + 1] = StackTraceElement(
            "com.ashalmawia.coriolan.AnotherClass",
            "myBelovedMethod",
            "AnotherClass.kt",
            57
    )
    processed[stacktrace.size + 2] = StackTraceElement(
            "com.ashalmawia.coriolan.inner.Inner",
            "makeInner",
            "Inners.kt",
            34
    )
    throwable.stackTrace = processed
    return throwable
}

fun assertThrowableEquals(one: Throwable, other: Throwable) {
    Assert.assertEquals(one.javaClass, other.javaClass)
    Assert.assertEquals(one.message, other.message)
    Assert.assertArrayEquals(one.stackTrace, other.stackTrace)
}