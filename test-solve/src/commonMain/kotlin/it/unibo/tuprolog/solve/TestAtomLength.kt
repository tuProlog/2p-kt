package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `atom_length/2` built-in, shared by every `Solver` implementation via the
 * `TestAtomLength.prototype(solverFactory)` factory (see `TestClassicAtomLength` in `:solve-classic` for a concrete
 * usage).
 */
interface TestAtomLength : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtomLengthImpl = TestAtomLengthImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_length(test,4).
     * ```
     * succeeds.
     *
     *
     */

    fun testAtomLengthNoVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_length(test,X).
     * ```
     * succeeds.
     *
     *
     */

    fun testAtomLengthSecondIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_length(X,4).
     * ```
     * fails.
     *
     *
     */

    fun testAtomLengthFirstIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_length(testLength,X).
     * ```
     * succeeds.
     *
     *
     */

    fun testAtomLengthSecondIsVar2()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_length(testLength,X).
     * ```
     * fails.
     *
     *
     */

    fun testAtomLengthFail()
}
