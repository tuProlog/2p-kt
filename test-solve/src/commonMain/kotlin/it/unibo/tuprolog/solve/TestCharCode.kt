package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `char_code/2` built-in, shared by every `Solver` implementation via the
 * `TestCharCode.prototype(solverFactory)` factory (see `TestClassicCharCode` in `:solve-classic` for a concrete
 * usage).
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
