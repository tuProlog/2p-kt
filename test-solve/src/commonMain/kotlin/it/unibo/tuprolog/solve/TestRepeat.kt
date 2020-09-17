package it.unibo.tuprolog.solve

interface TestRepeat : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestRepeat =
            TestRepeatImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- (repeat,!,fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testRepeat()
}
