package it.unibo.tuprolog.solve

/**
 * Tests of assertz
 */
interface TestAssertZ : SolverTest {
    companion object{
        fun prototype(solverFactory: SolverFactory): TestAssertZ =
                TestAssertZImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- assertz((foo(X) :- X -> call(X))).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAssertZClause()

    /**
     * Tests the queries
     * ```prolog
     * ?- assertz(_).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * Expected: `Halt(query=assertz(__16), exception=error(instantiation_error, 'Uninstantiated subgoal __17 in procedure assertz/3'))`
     * Actual	: `Halt(query=assertz(__16), exception=error(type_error(callable, __16), __16))`
     *
     */
    fun testAssertZAny()

    /**
     * Tests the queries
     * ```prolog
     * ?- assertz(4).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(callable, 4).
     */
    fun testAssertZNumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- assertz(foo := 4).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(callable, 4).
     *
     * ClauseDatabase can contain only well formed clauses: these aren't [foo :- 4]
     *
     */
    fun testAssertZFooNumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- assertz((atom(_) :- true)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception permission_error(modify,static_procedure,atom/1).
     *
     * Expected :No(query=assertz(atom(__15) :- true))
     * Actual   :Yes(query=assertz(atom(__15) :- true), substitution={})
     *
     */
    fun testAssertZAtomTrue()
}