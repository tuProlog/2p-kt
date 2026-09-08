package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `sub_atom/5` built-in (`sub_atom(Atom, Before, Length, After, SubAtom)`), shared by
 * every `Solver` implementation via the `TestSubAtom.prototype(solverFactory)` factory (see `TestClassicSubAtom` in
 * `:solve-classic` for a concrete usage).
 */
interface TestSubAtom : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSubAtom = TestSubAtomImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(abracadabra, 0, 1, 10, S).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `S` to `a`.
     */
    fun testSubAtomSubIsVar()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(abracadabra, 6, 5, 0, S).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `S` to `dabra`.
     */
    fun testSubAtomSubIsVar2()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(abracadabra, 3, L, 3, S).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `L` to `5` and variable `S` to `acada`.
     */
    fun testSubAtomSubIsVar3()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(banana, 3, 2, T, S).
     * ```
     * succeeds on a solver initialized with default built-ins and with an empty theory, producing 1 solution which
     * binds variable `T` to `1` and variable `S` to `an`.
     */
    fun testSubAtomDoubleVar4()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom('Banana', 3, 2, Y, S).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `instantiation_error` (the first argument is, despite appearances, an unbound variable named `Banana`, not the
     * atom `'Banana'`).
     */
    fun testSubAtomInstantiationError()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(5, 2, 2, _, S).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `type_error(atom, 5)`.
     */
    fun testSubAtomTypeErrorAtomIsInteger()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(banana, 4, 2, _, 2).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `type_error(atom, 2)`, since the last argument must be an atom (or unbound), not an integer.
     */
    fun testSubAtomTypeErrorSubIsInteger()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(banana, a, 2, _, S).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `type_error(integer, a)`.
     */
    fun testSubAtomTypeErrorBeforeIsNotInteger()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(banana, 4, n, _, S).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `type_error(integer, n)`.
     */
    fun testSubAtomTypeErrorLengthIsNotInteger()

    /**
     * Tests the query
     * ```prolog
     * ?- sub_atom(banana, 4, 2, m, S).
     * ```
     * fails on a solver initialized with default built-ins and with an empty theory, producing exception
     * `type_error(integer, m)`.
     */
    fun testSubAtomTypeErrorAfterIsNotInteger()
}
