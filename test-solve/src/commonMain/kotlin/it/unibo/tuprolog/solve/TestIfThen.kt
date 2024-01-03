package it.unibo.tuprolog.solve

interface TestIfThen : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestIfThen = TestIfThenImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- '->'(true, true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIfThenTrue()

    /**
     * Tests the queries
     * ```prolog
     * ?- '->'(true, fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIfThenFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- '->'(fail, true).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIfThenFailTrue()

    /**
     * Tests the queries
     * ```prolog
     * ?- '->'(true, X=1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `1`.
     */
    fun testIfThenXtoOne()

    /**
     * Tests the queries
     * ```prolog
     * ?- '->'(';'(X=1, X=2), true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `1`.
     */
    fun testIfThenXOr()

    /**
     * Tests the queries
     * ```prolog
     * ?- '->'(true, ';'(X=1, X=2)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds variable `X` to `1` and `X` to `2` .
     */
    fun testIfThenOrWithDoubleSub()
}
