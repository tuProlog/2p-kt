package it.unibo.tuprolog.solve

interface TestCatchAndThrow : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCatchAndThrow =
            TestCatchAndThrowImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- (catch(true, C, write('something')), throw(blabla)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `system_error`.
     */
    fun testCatchThrow()

    /**
     * Tests the queries
     * ```prolog
     * ?- catch(number_chars(A,L), error(instantiation_error, _), fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCatchFail()
}
