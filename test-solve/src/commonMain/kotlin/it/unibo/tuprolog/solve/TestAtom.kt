package it.unibo.tuprolog.solve

/**
 * Tests of atom
 */
interface TestAtom : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAtom =
            TestAtomImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(atom).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomAtom()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom('string').
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomString()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(a(b)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomAofB()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(Var).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomVar()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom([]).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomEmptyList()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(6).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- atom(3.3).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testAtomNumDec()

    /**
     * atom_chars Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_chars(X,[t,e,s,t]).
     * ?- atom_chars(test,[t,e,s,t]).
     * ?- atom_chars(test,[t,e,s,T]).
     * ?- atom_chars(test1,[t,e,s,T]).
     * ```
     */

    fun testAtomChars()

    /**
     * atom_codes Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_codes(abc,X).
     * ?- atom_codes(test,X).
     * ?- atom_codes(test,[116,101,115,116]).
     * ?- atom_codes(test,[112,101,115,116]).
     * ```
     */

    fun testAtomCodes()


    /**
     * atom_length Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_length(test,4).
     * ?- atom_length(test,X).
     * ?- atom_length(X,4).
     * ?- atom_length(42,X).
     * ?- atom_chars(test,5).
     * ```
     */
    fun testAtomLength()

    /**
     * atom_concat Testing
     *
     * Contained requests:
     * ```prolog
     * ?- atom_concat(test,concat,X).
     * ?- atom_concat(test,concat,test).
     * ?- atom_concat(test,X,testTest).
     * ```
     */

    fun testAtomConcat()

    /**
     * char_code Testing
     *
     * Contained requests:
     * ```prolog
     * ?- char_code(a,X).
     * ?- char_code(X,97).
     * ?- char_code(X,a).
     * ?- char_code(g,103).
     * ```
     */

    fun testCharCode()

    /** sub_atom Testing
     *
     * Contained requests:
     * ```prolog
     * ?- sub_atom(abracadabra, 0, 5, _, S2).
     * ?- sub_atom(abracadabra, _, 5, 0, S2),
     * ?-sub_atom(abracadabra, 3, L, 3, S2),
     * ?-sub_atom(abracadabra, Before, 2, After, ab),
     * ?-sub_atom('Banana', 3, 2, _, S2),
     * ?-
     * ```
     */

    fun testSubAtom()
}
