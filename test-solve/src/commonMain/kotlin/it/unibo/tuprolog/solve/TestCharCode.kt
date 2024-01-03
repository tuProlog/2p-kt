package it.unibo.tuprolog.solve

/**
 * char_code Testing
 *
 * Contained requests:
 * ```prolog
 * ?- char_code(a,X).
 * ?- char_code(X,97).
 * ?- char_code(X,a).
 * ?- char_code(g,104).
 * ```
 */
interface TestCharCode : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCharCodeImpl = TestCharCodeImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- char_code(a,X).
     * ```
     * succeeds.
     *
     */
    fun testCharCodeSecondIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- char_code(X,97).
     * ```
     * succeeds.
     *
     */

    fun testCharCodeFirstIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- char_code(X,a).
     * ```
     * Fails.
     *
     */

    fun testCharCodeTypeError()

    /**
     * Tests the queries
     * ```prolog
     * ?- char_code(g,104).
     * ```
     * Fails.
     *
     */

    fun testCharCodeFails()
}
