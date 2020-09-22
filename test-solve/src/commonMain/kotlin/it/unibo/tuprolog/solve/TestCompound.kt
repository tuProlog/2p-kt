package it.unibo.tuprolog.solve

interface TestCompound : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCompound =
            TestCompoundImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- compound(33.3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundDec()

    /**
     * Tests the queries
     * ```prolog
     * ?- compound(-33.3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundNegDec()

    /**
     * Tests the queries
     * ```prolog
     * ?- compound(-a).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundNegA()

    /**
     * Tests the queries
     * ```prolog
     * ?- compound(_).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundAny()

    /**
     * Tests the queries
     * ```prolog
     * ?- compound(a).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundA()

    /**
     * Tests the queries
     * ```prolog
     * ?- compound(a(b)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundAOfB()

    /**
     * Tests the queries
     * ```prolog
     * ?- compound([a]).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCompoundListA()
}
