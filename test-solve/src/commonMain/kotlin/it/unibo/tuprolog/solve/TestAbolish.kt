package it.unibo.tuprolog.solve

interface TestAbolish : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAbolish = TestAbolishImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- abolish(abolish/1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `permission_error(modify,static_procedure,abolish/1)`.
     */
    fun testDoubleAbolish()

    /**
     * Tests the queries
     * ```prolog
     * ?- abolish(foo/a).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(integer,a)`.
     */
    fun testAbolishFoo()

    /**
     * Tests the queries
     * ```prolog
     * ?- abolish(foo/(-1)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `domain_error(not_less_than_zero,-1)`.
     */
    fun testAbolishFooNeg()

    /**
     * Tests the queries
     * ```prolog
     * ?- (current_prolog_flag(max_arity,A), X is A + 1, abolish(foo/X)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `representation_error(max_arity)`.
     */
    fun testAbolishFlag()

    /**
     * Tests the queries
     * ```prolog
     * ?- abolish(5/2).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(atom,5)`.
     */
    fun testAbolish()
}
