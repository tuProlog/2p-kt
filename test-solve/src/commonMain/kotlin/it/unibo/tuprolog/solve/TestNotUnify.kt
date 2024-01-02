package it.unibo.tuprolog.solve

interface TestNotUnify : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNotUnify = TestNotUnifyImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- `\\=`(1,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testNumberNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- `\\=`(X,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testNumberXNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- `\\=`(X,Y).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testXYNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- ('\\='(X,Y),'\\='(X,abc)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testDoubleNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '\\='(f(X,def),f(def,Y)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFDefNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '\\='(1,2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testDiffNumberNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '\\='(1,1.0).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testDecNumberNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '\\='(g(X),f(f(X))).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testGNotUnifyFX()

    /**
     * Tests the query
     * ```prolog
     * ?- '\\='(f(X,1),f(a(X))).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFNotUnify()

    /**
     * Tests the query
     * ```prolog
     * ?- '\\='(f(X,Y,X),f(a(X),a(Y),Y,2)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testFMultipleTermNotUnify()
}
