package it.unibo.tuprolog.solve

interface TestSetOf : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSetOfImpl =
            TestSetOfImpl(solverFactory)
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
    fun testSetOfNoSorted()

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
