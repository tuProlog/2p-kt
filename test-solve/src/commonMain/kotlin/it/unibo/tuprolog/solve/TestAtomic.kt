package it.unibo.tuprolog.solve

/**
 * Tests of atomic
 */
interface TestAtomic {
    companion object{
        fun prototype(solverFactory: SolverFactory): TestAtomic =
                TestAtomicImpl(solverFactory)
    }

    /** A short test max duration */
    val shortDuration: TimeDuration
        get() = 250L

    /** A medium test max duration */
    val mediumDuration: TimeDuration
        get() = 2 * shortDuration

    /** A long test max duration */
    val longDuration: TimeDuration
        get() = 4 * mediumDuration

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