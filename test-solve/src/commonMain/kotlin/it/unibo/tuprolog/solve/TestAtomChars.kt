package it.unibo.tuprolog.solve

interface TestAtomChars : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtomCharsImpl =
            TestAtomCharsImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(X,[t,e,s,t]).
     * ```
     * succeeds.
     * producing 1 solution which binds variable `X` to `test`.
     *
     */

    fun atomCharsTestFirstIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(test,[t,e,s,t]).
     * ```
     * succeeds.
     *
     */

    fun atomCharsTestYes()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(test,[t,e,s,T]).
     * ```
     * succeeds.
     * producing 1 solution which binds variable `T` to `t`.
     *
     */

    fun atomCharsTestOneCharIsVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(test1,[t,e,s,T]).
     * ```
     * fail.
     */

    fun atomCharsTestFailure()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars('',L).
     * ```
     * fail.
     */

    fun atomCharsTestEmpty()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(ac, [A,C])
     * ```
     * succeeds.
     * producing 1 solution which binds variable `C` to `c`.
     */

    fun atomCharsTestListHead()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(A,L).
     * ```
     * fail.
     *
     */

    fun atomCharsTestIstantationErrorCheck()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom_chars(A,iso).
     * ```
     * fail.
     *
     */

    fun atomCharsTestTypeErrorCheck()
}
