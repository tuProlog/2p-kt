package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `atom_codes/2` built-in, shared by every `Solver` implementation via the
 * `TestAtomCodes.prototype(solverFactory)` factory (see `TestClassicAtomCodes` in `:solve-classic` for a concrete
 * usage).
 */
interface TestAtomCodes : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtomCodesImpl = TestAtomCodesImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_codes(abc,[97,98,99]).
     * ```
     * succeeds.
     *
     */

    fun testAtomCodesSecondIsVar1()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_codes(test,X).
     * ```
     * succeeds.
     *
     */

    fun testAtomCodesSecondIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_codes(test,[97,98,99]).
     * ```
     * succeeds.
     *
     */

    fun testAtomCodesFirstIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_codes(test,[116, 101, 115, 116]).
     * ```
     * succeeds.
     *
     */

    fun testAtomCodesNoVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_codes(test,[112, 101, 115, 116]).
     * ```
     * fail.
     *
     */

    fun testAtomCodesFail()
}
