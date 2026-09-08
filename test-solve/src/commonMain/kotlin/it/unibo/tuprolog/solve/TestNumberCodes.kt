package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `number_codes/2` built-in, shared by every `Solver` implementation via the
 * `TestNumberCodes.prototype(solverFactory)` factory (see `TestClassicNumberCodes` in `:solve-classic` for a
 * concrete usage). See also [TestNumberChars] for the analogous built-in working on single-character atoms.
 */
interface TestNumberCodes : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNumberCodesImpl = TestNumberCodesImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(33,L).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `L` to the code list `[51,51]` (the character codes of `'3'`).
     */
    fun testNumberCodesListIsVar()

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(33.1,L).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `L` to the code list `[51,51,46,49]` (the character codes of `"33.1"`).
     */
    fun testNumberCodesNumIsDecimal()

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(9921.1,L).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `L` to the code list for `"9921.1"`.
     */
    fun testNumberCodesListIsVar2()

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(33,[51,51]).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory.
     */
    fun testNumberCodesOk()

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(34,[51,52]).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory.
     */
    fun testNumberCodesCompleteTest()

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(X,[45,51,46,56]).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `X` to `-3.8`.
     */
    fun testNumberCodesNegativeNumber()

    /**
     * Tests the query
     * ```prolog
     * ?- number_codes(a,L).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `type_error(number, a)`.
     */
    fun testNumberCodesChar()
}
