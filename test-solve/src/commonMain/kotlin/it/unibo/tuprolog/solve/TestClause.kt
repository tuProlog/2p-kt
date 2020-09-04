package it.unibo.tuprolog.solve

interface TestClause : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestClause =
                TestClauseImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- clause(x,Body).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testClauseXBody()

    /**
     * Tests the queries
     * ```prolog
     * ?- clause(_,B).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testClauseAnyB()

    /**
     * Tests the queries
     * ```prolog
     * ?- clause(4,B).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,4)`.
     */
    fun testClauseNumB()

    /**
     * Tests the queries
     * ```prolog
     * ?- clause(f(_),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,5)`.
     */
    fun testClauseFAnyNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- clause(atom(_),Body).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `permission_error(access,private_procedure,atom/1)`.
     */
    fun testClauseAtomBody()
}