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
     *
     * Expected: `Yes(query=(asserta(bar(X_19) :- X_19), (bar(B_3), X_20) :- true), substitution={B_4=call(X_19)})`
     * Actual	: `No(query=(asserta(bar(X_19) :- X_19), (bar(B_3), X_20) :- true))`
     *
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
     * Expected: `Halt(query=asserta(__12), exception=error(instantiation_error, 'Uninstantiated subgoal __13 in procedure asserta/3'))`
     * Actual: `Halt(query=asserta(__12), exception=error(type_error(callable, __12), __12))`
     *
     */
    fun testAssertAAny()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta(4).
     * ```
     *
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(callable, 4).
     *
     */
    fun testAssertANumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta(foo := 4).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(callable, 4).
     *
     * ClauseDatabase can contain only well formed clauses: these aren't [foo :- 4]
     *
     */
    fun testAssertAFooNumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- asserta((atom(_) :- true)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception permission_error(modify,static_procedure,atom/1).
     *
     * Expected :No(query=asserta(atom(__14) :- true))
     * Actual   :Yes(query=asserta(atom(__14) :- true), substitution={})
     *
     */
    fun testAssertAAtomTrue()
}