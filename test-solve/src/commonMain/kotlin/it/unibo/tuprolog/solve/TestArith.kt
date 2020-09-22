package it.unibo.tuprolog.solve

/**
 * Tests of '=\=' (= arith_diff)
 */
interface TestArith : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestArith =
            TestArithImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- '=\\='(0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=\\='(1.0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=\\='(3*2,7-1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=\\='(N,5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * ```prolog
     * ?- '=\\='(floot(1),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(evaluable, floot/1).
     */
    fun testArithDiff()

    /**
     * Tests the queries
     * ```prolog
     * ?- '=:='(0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=:='(1.0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=:='(3*2,7-1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=:='(N,5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * ```prolog
     * ?- '=:='(floot(1),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(evaluable, floot/1).
     *
     * ```prolog
     * ?- 0.333 =:= 1/3.
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testArithEq()

    /**
     * Tests the queries
     * ```prolog
     * ?- '>'(0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '>'(1.0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '>'(3*2,7-1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '>'(X,5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * ```prolog
     * ?- '>'(2 + floot(1),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(evaluable, floot/1).
     */
    fun testArithGreaterThan()

    /**
     * Tests the queries
     * ```prolog
     * ?- '>='(0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '>='(1.0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '>='(3*2,7-1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '>='(X,5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * ```prolog
     * ?- '>='(2 + floot(1),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(evaluable, floot/1).
     */
    fun testArithGreaterThanEq()

    /**
     * Tests the queries
     * ```prolog
     * ?- '<'(0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '<'(1.0,1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '<'(3*2,7-1).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '<'(N,5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * ```prolog
     * ?- '<'(floot(1),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(evaluable, floot/1).
     */
    fun testArithLessThan()

    /**
     * Tests the queries
     * ```prolog
     * ?- '=<'(0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=<'(1.0,1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=<'(3*2,7-1).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     *
     * ```prolog
     * ?- '=<'(N,5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception instantiation_error.
     *
     * ```prolog
     * ?- '=<'(floot(1),5).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     * producing exception type_error(evaluable, floot/1).
     */
    fun testArithLessThanEq()
}
