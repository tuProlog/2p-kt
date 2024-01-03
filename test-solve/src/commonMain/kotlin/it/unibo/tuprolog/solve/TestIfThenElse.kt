package it.unibo.tuprolog.solve

interface TestIfThenElse : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestIfThenElse = TestIfThenElseImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(true, true), fail).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIfTrueElseFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(fail, true), true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIfFailElseTrue()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(true, fail), fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIfTrueThenElseFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(fail, true), fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testIfFailElseFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(true, X=1), X=2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `1`.
     */
    fun testIfXTrueElseX()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(fail, X=1), X=2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable`X` to `2` .
     */
    fun testIfFailElseX()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(true, ';'(X=1, X=2)), true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds variable `X` to `1` and `X` to `2`.
     */
    fun testIfThenElseOrWithDoubleSub()

    /**
     * Tests the queries
     * ```prolog
     * ?- ';'('->'(';'(X=1, X=2), true), true).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `1`.
     */
    fun testIfOrElseTrue()
}
