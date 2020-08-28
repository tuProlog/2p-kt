package it.unibo.tuprolog.solve

/**
 * Tests of Term diff, eq, gt, gt_eq, lt, lt_eq
 */
interface TestTerm : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTerm =
            TestTermImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- '\\=='(1,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(X,X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(1,2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(X,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(X,Y).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(_,_).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(X,a(X)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '\\=='(f(a),f(a)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testTermDiff()

    /**
     * Tests the queries
     * ```prolog
     * ?- '=='(1,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(X,X).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(1,2).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(X,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(X,Y).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(_,_).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(X,a(X)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=='(f(a),f(a)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testTermEq()

    /**
     * Tests the queries
     * ```prolog
     * ?- '@>'(1.0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>'(aardvark,zebra).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>'(short,short).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>'(short,shorter).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>'(foo(b),foo(a)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>'(X,X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>'(foo(a,X),foo(b,Y)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testTermGreaterThan()

    /**
     * Tests the queries
     * ```prolog
     * ?- '@>='(1.0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>='(aardvark,zebra).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>='(short,short).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>='(short,shorter).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>='(foo(b),foo(a)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>='(X,X).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@>='(foo(a,X),foo(b,Y)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testTermGreaterThanEq()

    /**
     * Tests the queries
     * ```prolog
     * ?- '@<'(1.0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@<'(aardvark,zebra).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@<'(short,short).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@<'(short,shorter).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@<'(foo(b),foo(a)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@<'(X,X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@<'(foo(a,X),foo(b,Y)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testTermLessThan()

    /**
     * Tests the queries
     * ```prolog
     * ?- '@=<'(1.0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@=<'(aardvark,zebra).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@=<'(short,short).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@=<'(short,shorter).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@=<'(foo(b),foo(a)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@=<'(X,X).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '@=<'(foo(a,X),foo(b,Y)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testTermLessThanEq()
}