package it.unibo.tuprolog.solve

interface TestStackTrace : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestStackTrace =
            TestStackTraceImpl(solverFactory)
    }

    fun testSimpleStackTrace()

    fun testDoubleStackTrace()

    fun testTripleStackTrace()

    fun testThrowIsNotInStacktrace()
}
