package it.unibo.tuprolog.solve

/**
 * Tests of number
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
