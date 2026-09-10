package it.unibo.tuprolog.solve

/**
 * Conformance tests for `, `/2 (conjunction, `and`), shared by every `Solver` implementation via the
 * `TestAnd.prototype(solverFactory)` factory (see `TestClassicAnd` in `:solve-classic` for a concrete usage).
 */
interface TestAnd : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAnd = TestAndImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- (X = 1, var(X)).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, since `X` is bound to `1`
     * by the left conjunct before `var(X)` is evaluated.
     */
    fun testTermIsFreeVariable()

    /**
     * Tests the query
     * ```prolog
     * ?- (var(X), X = 1).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `1`.
     */
    fun testWithSubstitution()

    /**
     * Tests the query
     * ```prolog
     * ?- (fail, call(3)).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory: since conjunction is evaluated
     * left-to-right and short-circuits on failure, the (otherwise erroneous) right conjunct is never resolved.
     */
    fun testFailIsCallable()

    /**
     * Tests the query
     * ```prolog
     * ?- (nofoo(X), call(X)).
     * ```
     * fails on a solver initialized with default built-ins, the `unknown` flag set to `error`, and an empty theory,
     * producing exception `existence_error(procedure, nofoo/1)`.
     */
    fun testNoFooIsCallable()

    /**
     * Tests the query
     * ```prolog
     * ?- (X = true, call(X)).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `true`.
     */
    fun testTrueVarCallable()
}
