package it.unibo.tuprolog.solve

/**
 * Conformance tests for `true/0`, shared by every `Solver` implementation via the
 * `TestTrue.prototype(solverFactory)` factory (see `TestClassicTrue` in `:solve-classic` for a concrete usage).
 */
interface TestTrue : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTrue = TestTrueImpl(solverFactory)
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
