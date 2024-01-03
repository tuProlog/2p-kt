package it.unibo.tuprolog.solve

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
