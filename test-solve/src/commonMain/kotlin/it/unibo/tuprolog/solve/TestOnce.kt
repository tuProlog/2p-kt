package it.unibo.tuprolog.solve

interface TestOnce : SolverTest {
    companion object {
        fun prototype(
            solverFactory: SolverFactory,
            errorSignature: Signature = Signature("once", 1)
        ): TestOnce = TestOnceImpl(solverFactory, errorSignature)
    }

    val errorSignature: Signature

    /**
     * Tests the queries
     * ```prolog
     * ?- once(!).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testOnceCut()

    /**
     * Tests the queries
     * ```prolog
     * ?- (once(!), (X=1; X=2)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds variable `X` to `1` and `X` to `2`.
     */
    fun testOnceCutOr()

    /**
     * Tests the queries
     * ```prolog
     * ?- once(repeat).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testOnceRepeat()

    /**
     * Tests the queries
     * ```prolog
     * ?- once(fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testOnceFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- once(3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable/3)`.
     */
    fun testOnceNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- once(X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testOnceX()
}
