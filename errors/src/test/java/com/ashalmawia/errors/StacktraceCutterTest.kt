package com.ashalmawia.errors

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class StacktraceCutterTest {

    @Test
    fun `test__regularException`() {
        // given
        val benchmark = IllegalStateException("some message")
        val error = IllegalStateException("some message")
        error.stackTrace = benchmark.stackTrace
        assertThrowableEquals(benchmark, error)

        val cutter = StacktraceCutterImpl

        // when
        val processed = cutter.cut(error)

        // then
        assertThrowableEquals(benchmark, processed)
    }

    @Test
    fun `test__regularException__withStubValues`() {
        // given
        val benchmark = addGeneralStacktraceElements(IllegalStateException("some message"))
        val error = IllegalStateException("some message")
        error.stackTrace = benchmark.stackTrace
        assertThrowableEquals(benchmark, error)

        val cutter = StacktraceCutterImpl

        // when
        val processed = cutter.cut(error)

        // then
        assertThrowableEquals(benchmark, processed)
    }

    @Test
    fun `test__exceptionCreatedByErrors`() {
        // given
        val message = "some state was illegal"
        val benchmark = IllegalStateException(message)
        val copy = IllegalStateException(message)
        copy.stackTrace = benchmark.stackTrace
        val throwable = addErrorsStacktraceElements(copy)

        val cutter = StacktraceCutterImpl

        // when
        val processed = cutter.cut(throwable)

        // then
        assertThrowableEquals(benchmark, processed)
    }

    @Test
    fun `test__exceptionCreatedByErrors__withStubValues`() {
        // given
        val message = "some state was illegal"
        val benchmark = addGeneralStacktraceElements(IllegalStateException(message))
        val copy = IllegalStateException(message)
        copy.stackTrace = benchmark.stackTrace
        val throwable = addErrorsStacktraceElements(copy)

        val cutter = StacktraceCutterImpl

        // when
        val processed = cutter.cut(throwable)

        // then
        assertThrowableEquals(benchmark, processed)
    }
}