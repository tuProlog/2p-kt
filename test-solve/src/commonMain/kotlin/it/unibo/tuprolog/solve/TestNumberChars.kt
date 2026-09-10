package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `number_chars/2` built-in, shared by every `Solver` implementation via the
 * `TestNumberChars.prototype(solverFactory)` factory (see `TestClassicNumberChars` in `:solve-classic` for a
 * concrete usage). See also [TestNumberCodes] for the analogous built-in working on character codes.
 */
interface TestNumberChars : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNumberCharsImpl = TestNumberCharsImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(33,L).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `L` to the list `['3','3']`.
     */
    fun testNumberCharsListIsVar()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(33,['3','3']).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory.
     */
    fun testNumberCharsOK()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(X,['3','3']).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `33`.
     */
    fun testNumberCharsNumIsVar()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(X,['-','2','5']).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `-25`.
     */
    fun testNumberCharsNumNegativeIsVar()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(X,['\n','3']).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `3` (leading whitespace is skipped while parsing the character list).
     */
    fun testNumberCharsSpace()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(X,['4','.','2']).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `4.2`.
     */
    fun testNumberCharsDecimalNumber()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(X,['3','.','9']).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `3.9`.
     */
    fun testNumberCharsCompleteCase()

    /**
     * Tests the query
     * ```prolog
     * ?- number_chars(X,L).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `instantiation_error` (both arguments unbound).
     */
    fun testNumberCharsInstationErrror()
}
