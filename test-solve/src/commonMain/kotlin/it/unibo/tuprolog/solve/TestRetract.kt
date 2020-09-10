package it.unibo.tuprolog.solve

interface TestRetract : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestRetract =
            TestRetractImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- retract((4 :- X)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `domain_error(clause, 4))`.
     */
    fun testRetractNumIfX()

    /**
     * Tests the queries
     * ```prolog
     * ?- retract((atom(_) :- X == '[]')).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `permission_error(modify,private_procedure,atom/1)`.
     */
    fun testRetractAtomEmptyList()
}