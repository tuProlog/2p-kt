package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `repeat/0` built-in, shared by every `Solver` implementation via the
 * `TestRepeat.prototype(solverFactory)` factory (see `TestClassicRepeat` in `:solve-classic` for a concrete usage).
 */
interface TestRepeat : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestRepeat = TestRepeatImpl(solverFactory)
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
