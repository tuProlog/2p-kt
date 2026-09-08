package it.unibo.tuprolog.solve

/**
 * Conformance tests for `fail/0`, plus the `unknown` flag's effect on resolving an undefined procedure, shared by
 * every `Solver` implementation via the `TestFail.prototype(solverFactory)` factory (see `TestClassicFail` in
 * `:solve-classic` for a concrete usage).
 */
interface TestFail : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestFail = TestFailImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- fail.
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- undef_pred.
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `existence_error(procedure, undef_pred/0)`.
     */
    fun testUndefPred()

    /**
     * Tests the queries
     * ```prolog
     * ?- (set_prolog_flag(unknown, fail), undef_pred).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testSetFlagFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- (set_prolog_flag(unknown, warning), undef_pred).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testSetFlagWarning()
}
