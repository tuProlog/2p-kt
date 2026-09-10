package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `setof/3` built-in, shared by every `Solver` implementation via the
 * `TestSetOf.prototype(solverFactory)` factory (see `TestClassicSetOf` in `:solve-classic` for a concrete usage).
 * Compare with [TestBagOf], which does not sort or deduplicate results.
 */
interface TestSetOf : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSetOfImpl = TestSetOfImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- setof(X,(X=1;X=2),L)
     * ```
     */
    fun testSetOfBasic()

    /**
     * Tests the queries
     * ```prolog
     * ?- setof(X,(X=1;X=2),X)
     * ```
     */
    fun testSetOfX()

    /**
     * Tests the queries
     * ```prolog
     * ?- setof(X,(X=2;X=1),L)
     * ```
     */
    fun testSetOfSorted()

    /**
     * Tests the queries
     * ```prolog
     * ?- setof(X,(X=2;X=2),L)
     * ```
     */
    fun testSetOfDoubled()

    /**
     * Tests the queries
     * ```prolog
     * ?- setof(X,fail,L)
     * ```
     */
    fun testSetOfFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- setof(X,Y^((X=1,Y=1);(X=2,Y=2)),S)
     * ```
     */
    fun testSetOfAsFindAll()
}
