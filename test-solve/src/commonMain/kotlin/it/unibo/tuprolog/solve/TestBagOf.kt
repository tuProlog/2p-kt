package it.unibo.tuprolog.solve

/**
 * Conformance tests for the ISO `bagof/3` built-in, shared by every `Solver` implementation via the
 * `TestBagOf.prototype(solverFactory)` factory (see `TestClassicBagOf` in `:solve-classic` for a concrete usage).
 * Compare with [TestSetOf] (which additionally sorts and deduplicates results) and [TestFindAll] (which additionally
 * never fails and does not support the `^`/2 existential-quantification operator).
 */
interface TestBagOf : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestBagOfImpl = TestBagOfImpl(solverFactory)
    }

    /**
     * Tests the queries
     * ```prolog
     * ?- bagof(X,(X=1;X=2),L)
     * ```
     */
    fun testBagXInDifferentValues()

    /**
     * Tests the queries
     * ```prolog
     * ?-bagof(X,(X=1;X=2),X)
     * ```
     */
    fun testBagOfFindX()

    /**
     * Tests the queries
     * ```prolog
     * ?- bagof(X,(X=Y;X=Z),L)
     * ```
     */
    fun testBagOfYXZ()

    /**
     * Tests the queries
     * ```prolog
     * ?- bagof(X,fail,L).
     * ```
     */
    fun testBagOfFail()

    /**
     * Tests the queries
     * ```prolog
     * ?- bagof(X,Y^((X=1,Y=2)),S).
     * ```
     */
    fun testBagOfSameAsFindall()

    /**
     * Tests the queries
     * ```prolog
     * ?-bagof(X,Y^Z,L)
     * ```
     */
    fun testBagOfInstanceError()

    /**
     * Tests the queries
     * ```prolog
     * ?- bagof(X,1,L)
     * ```
     */
    fun testBagOfTypeError()
}
