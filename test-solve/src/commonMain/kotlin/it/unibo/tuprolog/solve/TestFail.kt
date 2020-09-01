package it.unibo.tuprolog.solve

interface TestFail: SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestFail =
                TestFailImpl(solverFactory)
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