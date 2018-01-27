package com.ashalmawia.errors

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

@RunWith(BlockJUnit4ClassRunner::class)
class ErrorReporterTest {

    @Test(expected = UnknownError::class)
    fun testStrictReporter() {
        // given
        val reporter = StrictErrorReporter()
        val tag = "MY_TAG"
        val error = UnknownError("sample error")

        // when
        reporter.report(tag, error)
    }

    @Test
    fun testSoftReporter() {
        // given
        val logger = MockLogger()
        val reporter = SoftErrorReporter(listOf(logger))
        val tag = "MY_TAG"
        val error = UnknownError("sample error")

        // when
        reporter.report(tag, error)

        // then
        assertEquals("tag was correct", tag, logger.lastTag)
        assertEquals("error was correct", error, logger.lastError)
        assertEquals("logging was done once", 1, logger.timesCalled)
    }
}