package it.unibo.tuprolog.solve

/**
 * Tests of findall
 */
interface TestFindAll {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestFindAll =
                TestFindAllImpl(solverFactory)
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
     * ?- findall(X,(X=1 ; X=2),S).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `S` to the list `[1, 2]`.
     */
    fun testFindXInDiffValues()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X+Y,(X=1),S).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `S` to `[+(1, Y)]`.
     */
    fun testFindSumResult()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X,fail,L).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `L` to the empty list.
     */
    fun testFindXinFail()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X,(X=1 ; X=1),S).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `S` the list `[1, 1]`.
     */
    fun testFindXinSameXValues()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X,(X=2 ; X=1),[1,2]).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testResultListIsCorrect()

    /**
     * Tests the query
     * ```prolog
     * findall(X,(X=1 ; X=2),[X,Y]).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 2 solutions which bind variable `X` to the value `1` and `Y` to the value `2`, respectively.
     */
    fun testFindXtoDoubleAssigment()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X,Goal,S).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testFindXinGoal()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X,4,S).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable, 4)`.
     */
    fun testFindXinNumber()

    /**
     * Tests the query
     * ```prolog
     * ?- findall(X,call(1),S).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable, 1)`.
     */
    fun testFindXinCall()
}