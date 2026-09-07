package it.unibo.tuprolog.solve

/**
 * Conformance tests for `!`/0 (cut), shared by every `Solver` implementation via the
 * `TestCut.prototype(solverFactory)` factory (see `TestClassicCut` in `:solve-classic` for a concrete usage).
 */
interface TestCut : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCut = TestCutImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- !.
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCut()

    /**
     * Tests the queries
     * ```prolog
     * ?- (!,fail;true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCutFailTrue()

    /**
     * Tests the queries
     * ```prolog
     * ?- (call(!),fail;true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCallCutFailTrue()
}
