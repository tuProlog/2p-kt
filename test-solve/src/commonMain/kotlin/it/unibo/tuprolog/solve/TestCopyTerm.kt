package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `copy_term/2` built-in, shared by every `Solver` implementation via the
 * `TestCopyTerm.prototype(solverFactory)` factory (see `TestClassicCopyTerm` in `:solve-classic` for a concrete
 * usage).
 */
interface TestCopyTerm : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCopyTerm = TestCopyTermImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(X,3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCopyXNum()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(_,a).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCopyAnyA()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(a+X,X+b).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     *  producing 1 solution which binds variable `X` to `a`.
     */
    fun testCopySum()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(_,_).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCopyAnyAny()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(X+X+Y,A+B+B).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     *  producing 1 solution which binds variable `B` to `A`.
     */
    fun testCopyTripleSum()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(a,a).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCopyAA()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(a,b).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testCopyAB()

    /**
     * Tests the queries
     * ```prolog
     * ?- copy_term(f(a),f(X)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds variable `X` to `A`.
     */
    fun testCopyF()

    /**
     * Tests the queries
     * ```prolog
     * ?- (copy_term(a+X,X+b),copy_term(a+X,X+b)).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory.
     */
    fun testDoubleCopy()
}
