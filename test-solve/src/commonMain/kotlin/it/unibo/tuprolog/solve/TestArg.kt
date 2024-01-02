package it.unibo.tuprolog.solve

/**
 * Tests of Arg
 */
interface TestArg : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestArg = TestArgImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- arg(1,foo(a,b),a).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testArgFromFoo()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(1,foo(a,b),X).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `a`.
     */
    fun testArgFromFooX()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(1,foo(X,b),a).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `a`.
     */
    fun testArgFromFoo2()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(2,foo(a, f(X,b), c), f(a, Y)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds variable `X` to `a` and variable `Y` to `b`.
     */
    fun testArgFromFooInF()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(1,foo(X,b),Y).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to variable `Y`.
     */
    fun testArgFromFooY()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(1,foo(a,b),b).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testArgFromFooInSecondTerm()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(0,foo(a,b),foo).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testArgFromFooInFoo()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(3,foo(3,4),N).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testArgNumberFromFoo()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(X,foo(a,b),a).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception instantiation_error.
     */
    fun testArgXFromFoo()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(1,X,a).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception instantiation_error.
     */
    fun testArgNumberFromX()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(0,atom,A).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception type_error(compound, atom).
     */
    fun testArgFromAtom()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(0,3,A).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception type_error(compound, 3).
     */
    fun testArgFromNumber()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(-3,foo(a,b),A).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception domain_error(not_less_than_zero,-3).
     */
    fun testNegativeArgFromFoo()

    /**
     * Tests the query
     * ```prolog
     * ?- arg(a,foo(a,b),X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception type_error(integer,a).
     */
    fun testArgAFromFoo()
}
