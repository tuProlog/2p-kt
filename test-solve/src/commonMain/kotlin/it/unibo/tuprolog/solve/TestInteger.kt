package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `integer/1` type-checking built-in, shared by every `Solver` implementation via the
 * `TestInteger.prototype(solverFactory)` factory (see `TestClassicInteger` in `:solve-classic` for a concrete usage).
 */
interface TestInteger : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestInteger = TestIntegerImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- integer(3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIntPositiveNum()

    /**
     * Tests the query
     * ```prolog
     * ?- integer(-3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIntNegativeNum()

    /**
     * Tests the query
     * ```prolog
     * ?- integer(3.3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIntDecNum()

    /**
     * Tests the query
     * ```prolog
     * ?- integer(X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIntX()

    /**
     * Tests the query
     * ```prolog
     * ?- integer(atom).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testIntAtom()
}
