package it.unibo.tuprolog.solve

/**
 * Tests of ';'/2 (= or, disjunction)
 */
interface TestOr {

    companion object{
        fun prototype(solverFactory: SolverFactory): TestOr =
                TestOrImpl(solverFactory)
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
     * Tests the query
     * ```prolog
     * ?- ';'(true, fail).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution with no variable bindings.
     */
    fun testTrueOrFalse()

    /**
     * Tests the query
     * ```prolog
     * ';'((!, fail), true).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCutFalseOrTrue()

    /**
     * Tests the query
     * ```prolog
     * ';'(!, call(3)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution with no variable bindings.
     */
    fun testCutCall()

    /**
     * Tests the query
     * ```prolog
     * ';'((X=1, !), X=2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to the value `1`.
     */
    fun testCutAssignedValue()

    /**
     * Tests the query
     * ```prolog
     * ';'(X=1, X=2).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which bind variable `X` to the values `1` and `2`, respectively.
     */
    fun testOrDoubleAssignment()
}