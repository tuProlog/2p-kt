package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `nonvar/1` type-checking built-in, shared by every `Solver` implementation via the
 * `TestNonVar.prototype(solverFactory)` factory (see `TestClassicNonVar` in `:solve-classic` for a concrete usage).
 */
interface TestNonVar : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestNonVar = TestNonVarImpl(solverFactory)
    }

    /**
     * Tests the query
     * ```prolog
     * ?- nonvar(33.3).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution with no variable bindings.
     */
    fun testNonVarNumber()

    /**
     * Tests the query
     * ```prolog
     * ?- nonvar(foo).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution with no variable bindings.
     */
    fun testNonVarFoo()

    /**
     * Tests the query
     * ```prolog
     * ?- nonvar(Foo).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory
     */
    fun testNonVarFooCl()

    /**
     * Tests the query
     * ```prolog
     * ?- (foo=Foo,nonvar(Foo)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution which binds `Foo` to `foo`.
     */
    fun testNonVarFooAssignment()

    /**
     * Tests the query
     * ```prolog
     * ?- nonvar(_).
     * ```
     * fails on a solver initialized with default built-ins and with and empty theory
     */
    fun testNonVarAnyTerm()

    /**
     * Tests the query
     * ```prolog
     * ?- nonvar(a(b)).
     * ```
     * succeeds on a solver initialized with default built-ins and with and empty theory,
     * producing 1 solution with no variable bindings.
     */
    fun testNonVar()
}
