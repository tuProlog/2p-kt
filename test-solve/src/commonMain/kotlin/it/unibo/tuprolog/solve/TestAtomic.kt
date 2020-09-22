package it.unibo.tuprolog.solve

/**
 * Tests of atomic
 */
interface TestAtomic : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtomic =
            TestAtomicImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atomic(atom).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomicAtom()

    /**
     * Tests the queries
     * ```prolog
     * ?- atomic(a(b)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomicAofB()

    /**
     * Tests the queries
     * ```prolog
     * ?- atomic(Var).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomicVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atomic([]).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomicEmptyList()

    /**
     * Tests the queries
     * ```prolog
     * ?- atomic(6).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomicNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- atomic(3.3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomicNumDec()
}
