package it.unibo.tuprolog.solve

/**
 * Conformance tests for `\+`/1 (negation as failure), shared by every `Solver` implementation via the
 * `TestNotProvable.prototype(solverFactory)` factory (see `TestClassicNotProvable` in `:solve-classic` for a
 * concrete usage).
 */
interface TestNotProvable : SolverTest {
    companion object {
        fun prototype(
            solverFactory: SolverFactory,
            errorSignature: Signature = Signature("not", 1),
        ): TestNotProvable = TestNotProvableImpl(solverFactory, errorSignature)
    }

    /** The [Signature] expected in errors raised while resolving the goal passed to `\+/1`. */
    val errorSignature: Signature

    /**
     * Tests the queries
     * ```prolog
     * ?- \+(true).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testNPTrue()

    /**
     * Tests the queries
     * ```prolog
     * ?- \+(!).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testNPCut()

    /**
     * Tests the queries
     * ```prolog
     * ?- \+(!,fail).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testNPCutFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- ((X = 1;X = 2), \+((!,fail))).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which binds variable `X` to `1` and `X` to `2`.
     */
    fun testOrNotCutFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- \+(4 = 5).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testNPEquals()

    /**
     * Tests the queries
     * ```prolog
     * ?- \+(3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable, 3)`.
     */
    fun testNPNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- \+(X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testNPX()
}
