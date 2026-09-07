package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `compound/1` type-checking built-in, shared by every `Solver` implementation via
 * the `TestCompound.prototype(solverFactory)` factory (see `TestClassicCompound` in `:solve-classic` for a concrete
 * usage).
 */
interface TestCompound : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCompound = TestCompoundImpl(solverFactory)
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
