package it.unibo.tuprolog.solve

interface TestIs : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestIs =
            TestIsImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- 'is'(Result,3 + 11.0).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `Result` to `14.0`.
     */
    fun testIsResult()

    /**
     * Tests the queries
     * ```prolog
     * ?- (X = 1 + 2, 'is'(Y, X * 3)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds variable `X` to `1+2` and `Y` to `9`.
     */
    fun testIsXY()

    /**
     * Tests the queries
     * ```prolog
     * ?- 'is'(foo,77).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testIsFoo()

    /**
     * Tests the queries
     * ```prolog
     * ?- 'is'(77, N).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testIsNNumber()

    /**
     * Tests the queries
     * ```prolog
     * ?- 'is'(77, foo).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(foo/0)`.
     */
    fun testIsNumberFoo()

    /**
     * Tests the queries
     * ```prolog
     * ?- 'is'(X,float(3)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `3.0`.
     */
    fun testIsXFloat()
}