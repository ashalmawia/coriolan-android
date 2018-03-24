package com.ashalmawia.errors

import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ErrorsTest {

    private val mockCutter = MockStacktraceCutter()

    @Test
    fun `test__illegalArgument`() {
        // given
        val reporter = MockReporter()
        val tag = "EX_TAG"
        val message = "some error message about illegal argument"
        val errors = ErrorsImpl(reporter, mockCutter)

        // when
        errors.illegalArgument(tag, message)

        // then
        assertEquals("tag was correct", tag, reporter.lastTag)
        assertNotNull("error was reported", reporter.lastError)
        assertEquals("error type was correct", IllegalArgumentException::class, reporter.lastError!!::class)
        assertEquals("error message was correct", message, reporter.lastError!!.message)
        assertEquals("report was done once", 1, reporter.timesCalled)
    }

    @Test
    fun `test__illegalState`() {
        // given
        val reporter = MockReporter()
        val tag = "EX_TAG"
        val message = "some error message about illegal state"
        val errors = ErrorsImpl(reporter, mockCutter)

        // when
        errors.illegalState(tag, message)

        // then
        assertEquals("tag was correct", tag, reporter.lastTag)
        assertNotNull("error was reported", reporter.lastError)
        assertEquals("error type was correct", IllegalStateException::class, reporter.lastError!!::class)
        assertEquals("error message was correct", message, reporter.lastError!!.message)
        assertEquals("report was done once", 1, reporter.timesCalled)
    }

    @Test
    fun `test__generalError`() {
        // given
        val reporter = MockReporter()
        val tag = "EX_TAG"
        val error = UnknownError("some general error")
        val errors = ErrorsImpl(reporter, mockCutter)

        // when
        errors.error(tag, error)

        // then
        assertEquals("tag was correct", tag, reporter.lastTag)
        assertEquals("error was correct", error, reporter.lastError)
        assertEquals("report was done once", 1, reporter.timesCalled)
    }
}

@RunWith(JUnit4::class)
class StacktracedErrorsTest {

    @Test
    fun `test__illegalArgument__stacktrace`() {
        // given
        val reporter = MockReporter()
        val tag = "EX_TAG"
        val message = "some error message about illegal state"
        val errors = Errors.Builder().reporter(reporter).build()

        // when
        errors.illegalArgument(tag, message); val benchmark = IllegalArgumentException(message)       // must be the same line

        // then
        Assert.assertNotNull(reporter.lastError)
        assertThrowableEquals(benchmark, reporter.lastError!!)
    }

    @Test
    fun `test__illegalState__stacktrace`() {
        // given
        val reporter = MockReporter()
        val tag = "EX_TAG"
        val message = "some error message about illegal state"
        val errors = Errors.Builder().reporter(reporter).build()

        // when
        errors.illegalState(tag, message); val benchmark = IllegalStateException(message)       // must be the same line

        // then
        Assert.assertNotNull(reporter.lastError)
        assertThrowableEquals(benchmark, reporter.lastError!!)
    }
}