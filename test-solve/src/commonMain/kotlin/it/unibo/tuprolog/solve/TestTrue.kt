package it.unibo.tuprolog.solve

interface TestTrue : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTrue =
                TestTrueImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- true.
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testTrue()
}