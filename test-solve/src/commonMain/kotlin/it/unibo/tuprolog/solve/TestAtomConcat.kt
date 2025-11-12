package it.unibo.tuprolog.solve

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
