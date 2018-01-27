@file:Suppress("unused", "MemberVisibilityCanPrivate")

package com.ashalmawia.errors

interface Errors {

    fun illegalArgument(tag: String, message: String)
    fun illegalState(tag: String, message: String)
    fun error(tag: String, exception: Throwable)

    companion object {
        private lateinit var instance: Errors

        fun with(errors: Errors) {
            instance = errors
        }

        fun illegalArgument(tag: String, message: String) { instance.illegalArgument(tag, message) }
        fun illegalState(tag: String, message: String) { instance.illegalState(tag, message) }
        fun error(tag: String, exception: Throwable) { instance.error(tag, exception) }
    }

    class Builder {
        private var reporter: ErrorReporter = StrictErrorReporter()
        private var loggers: MutableList<Logger> = mutableListOf(
                AndroidLogger()
        )
        private var cutter: StacktraceCutter = StacktraceCutterImpl

        fun build(): Errors {
            return ErrorsImpl(reporter, cutter)
        }

        fun addLogger(logger: Logger): Builder {
            loggers.add(logger)
            return this
        }

        fun useCrashlytics(): Builder {
            loggers.add(CrashlyticsLogger())
            return this
        }

        fun debug(debug: Boolean): Builder {
            if (debug) strict() else soft()
            return this
        }

        fun strict(): Builder {
            this.reporter = StrictErrorReporter()
            return this
        }

        fun soft(): Builder {
            this.reporter = SoftErrorReporter(loggers)
            return this
        }

        fun reporter(reporter: ErrorReporter): Builder {
            this.reporter = reporter
            return this
        }

        fun stacktraceCutter(cutter: StacktraceCutter): Builder {
            this.cutter = cutter
            return this
        }
    }
}

internal class ErrorsImpl(private val reporter: ErrorReporter, private val cutter: StacktraceCutter) : Errors {

    override fun illegalArgument(tag: String, message: String) {
        reporter.report(tag, cutter.cut(IllegalArgumentException(message)))
    }

    override fun illegalState(tag: String, message: String) {
        reporter.report(tag, cutter.cut(IllegalStateException(message)))
    }

    override fun error(tag: String, exception: Throwable) {
        reporter.report(tag, exception)
    }
}