package it.unibo.tuprolog.solve

interface TestUnify : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestUnify =
            TestUnifyImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- `=`(1,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testNumberUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- `=`(X,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to the value `1`.
     */
    fun testNumberXUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- `=`(X,Y).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `Y` to the variable `X`.
     */
    fun testXYUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- ('='(X,Y),'='(X,abc)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solution which binds variable `X` to `abc` and the variable `Y` to `abc`.
     */
    fun testDoubleUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(f(X,def),f(def,Y)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solution which binds variable `X` to `abc` and the variable `Y` to `abc`.
     */
    fun testFDefUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(1,2).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testDiffNumberUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(1,1.0).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testDecNumberUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(g(X),f(f(X))).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testGUnifyFX()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(f(X,1),f(a(X))).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(f(X,Y,X),f(a(X),a(Y),Y,2)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFMultipleTermUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '='(f(A,B,C),f(g(B,B),g(C,C),g(D,D))).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 3 solutions which binds variable `A` to `g(g(g(D,D),g(D,D)),g(g(D,D),g(D,D)))`,
     * the variable `B` to `g(g(D,D),g(D,D))` and the variable `C` to `g(D,D)`.
     */
    fun testMultipleTermUnify()
}
