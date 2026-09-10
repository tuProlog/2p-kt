package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `float/1` type-checking built-in, shared by every `Solver` implementation via the
 * `TestFloat.prototype(solverFactory)` factory (see `TestClassicFloat` in `:solve-classic` for a concrete usage).
 */
interface TestFloat : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestFloat = TestFloatImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- float(3.3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFloatDec()

    /**
     * Tests the queries
     * ```prolog
     * ?- float(-3.3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFloatDecNeg()

    /**
     * Tests the queries
     * ```prolog
     * ?- float(3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFloatNat()

    /**
     * Tests the queries
     * ```prolog
     * ?- float(atom).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFloatAtom()

    /**
     * Tests the queries
     * ```prolog
     * ?- float(X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testFloatX()
}
