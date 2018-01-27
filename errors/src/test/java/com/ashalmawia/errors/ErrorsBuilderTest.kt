package com.ashalmawia.errors

import org.junit.Assert.*
import org.junit.Test

class ErrorsBuilderTest {

    @Test
    fun `test__addLogger__AND__soft`() {
        // given
        val logger = MockLogger()
        val builder = Errors.Builder()

        val tag = "SOME_TAG"
        val error = UnknownError("some error")

        // when
        builder.addLogger(logger)
        val errors = builder.soft().build()
        errors.error(tag, error)

        // then
        assertEquals("tag was correct", tag, logger.lastTag)
        assertEquals("error was correct", error, logger.lastError)
        assertEquals("report was done once", 1, logger.timesCalled)
    }

    @Test
    fun `test__debugTrue`() {
        // given
        val logger = MockLogger()
        val builder = Errors.Builder()

        val tag = "SOME_TAG"
        val error = UnknownError("some error")

        // when
        val errors = builder.addLogger(logger).debug(false).build()
        errors.error(tag, error)

        // then
        assertEquals("tag was correct", tag, logger.lastTag)
        assertEquals("error was correct", error, logger.lastError)
        assertEquals("report was done once", 1, logger.timesCalled)
    }

    @Test(expected = UnknownError::class)
    fun `test__debugFalse`() {
        // given
        val logger = MockLogger()
        val builder = Errors.Builder()

        val tag = "SOME_TAG"
        val error = UnknownError("some error")

        // when
        val errors = builder.addLogger(logger).debug(true).build()
        errors.error(tag, error)
    }

    @Test(expected = UnknownError::class)
    fun `test__strict`() {
        // given
        val logger = MockLogger()
        val builder = Errors.Builder()

        val tag = "SOME_TAG"
        val error = UnknownError("some error")

        // when
        val errors = builder.addLogger(logger).strict().build()
        errors.error(tag, error)
    }

    @Test
    fun `test__reporter`() {
        // given
        val reporter = MockReporter()
        val builder = Errors.Builder()

        val tag = "SOME_TAG"
        val error = UnknownError("some error")

        // when
        val errors = builder.reporter(reporter).build()
        errors.error(tag, error)

        // then
        assertEquals("tag was correct", tag, reporter.lastTag)
        assertEquals("error was correct", error, reporter.lastError)
        assertEquals("report was done once", 1, reporter.timesCalled)
    }
}