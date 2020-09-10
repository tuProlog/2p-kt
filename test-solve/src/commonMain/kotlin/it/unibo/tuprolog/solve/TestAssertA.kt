package it.unibo.tuprolog.solve

/**
 * Tests of asserta
 */
interface TestAssertA : SolverTest{
    companion object{
        fun prototype(solverFactory: SolverFactory): TestAssertA =
                TestAssertAImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- (asserta((bar(X) :- X)), clause(bar(X), B)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     * producing 1 solution which binds variable `B` to `call(X)`.
     */
    fun testAssertAClause()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta(_).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     */
    fun testAssertAAny()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta(4).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(callable, 4).
     */
    fun testAssertANumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta(foo :- 4).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception domain_error(clause, foo :- 4)
     */
    fun testAssertAFooNumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta((atom(_) :- true)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception permission_error(modify,private_procedure,atom/1).
     */
    fun testAssertAAtomTrue()
}