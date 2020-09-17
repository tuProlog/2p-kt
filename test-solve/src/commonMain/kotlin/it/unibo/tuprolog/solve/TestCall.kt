package it.unibo.tuprolog.solve

interface TestCall : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCall =
            TestCallImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- call(!).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCallCut()

    /**
     * Tests the queries
     * ```prolog
     * ?- call(fail).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCallFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- call(fail,X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCallFailX()

    /**
     * Tests the queries
     * ```prolog
     * ?- call(fail, call(1)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     */
    fun testCallFailCall()

    /**
     * Tests the queries
     * ```prolog
     * ?- call((write(3), X)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testCallWriteX()

    /**
     * Tests the queries
     * ```prolog
     * ?- call((write(3), call(1))).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,1)`.
     */
    fun testCallWriteCall()

    /**
     * Tests the queries
     * ```prolog
     * ?- call(X).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `instantiation_error`.
     */
    fun testCallX()

    /**
     * Tests the queries
     * ```prolog
     * ?- call(1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,1)`.
     */
    fun testCallOne()

    /**
     * Tests the queries
     * ```prolog
     * ?- call((fail,1)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,(fail,1))`.
     */
    fun testCallFailOne()

    /**
     * Tests the queries
     * ```prolog
     * ?- call((write(3),1)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,(write(3),1))`.
     */
    fun testCallWriteOne()

    /**
     * Tests the queries
     * ```prolog
     * ?- call((1; true)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory,
     * producing exception `type_error(callable,(1; true))`.
     */
    fun testCallTrue()
}
