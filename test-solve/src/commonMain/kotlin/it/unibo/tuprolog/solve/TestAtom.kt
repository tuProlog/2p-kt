package it.unibo.tuprolog.solve

/**
 * Tests of atom
 */
interface TestAtom : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtom = TestAtomImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(atom).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomAtom()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom('string').
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomString()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(a(b)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomAofB()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(Var).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom([]).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomEmptyList()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(6).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(3.3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomNumDec()
}
