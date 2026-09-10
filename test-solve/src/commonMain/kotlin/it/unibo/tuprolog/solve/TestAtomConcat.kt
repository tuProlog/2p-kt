package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `atom_concat/3` built-in, shared by every `Solver` implementation via the
 * `TestAtomConcat.prototype(solverFactory)` factory (see `TestClassicAtomConcat` in `:solve-classic` for a concrete
 * usage).
 */
interface TestAtomConcat : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtomConcatImpl = TestAtomConcatImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_concat(test,concat,X).
     * ```
     * succeeds.
     *
     */

    fun testAtomConcatThirdIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_concat(test,concat,test).
     * ```
     * fails.
     *
     */

    fun testAtomConcatFails()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_concat(concat,X,testconcat).
     * ```
     * fails.
     *
     */

    fun testAtomConcatFailsNotPrefix()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_concat(X,test,testconcat).
     * ```
     * fails.
     *
     */

    fun testAtomConcatFailsNotSuffix()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_concat(test,X,testTest).
     * ```
     * succeeds.
     *
     */

    fun testAtomConcatSecondIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_concat(X,query,testquery).
     * ```
     * succeeds.
     *
     */

    fun testAtomConcatFirstIsVar()
}
