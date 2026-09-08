package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `number/1` type-checking built-in, shared by every `Solver` implementation via the
 * `TestNumber.prototype(solverFactory)` factory (see `TestClassicNumber` in `:solve-classic` for a concrete usage).
 */
interface TestNumber : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNumber = TestNumberImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- number(3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testBasicNum()

    /**
     * Tests the query
     * ```prolog
     * ?- number(3.3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testDecNum()

    /**
     * Tests the query
     * ```prolog
     * ?- number(-3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testNegNum()

    /**
     * Tests the query
     * ```prolog
     * ?- number(a).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testLetterNum()

    /**
     * Tests the query
     * ```prolog
     * ?- number(X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testXNum()
}
