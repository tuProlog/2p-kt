package it.unibo.tuprolog.solve

/** A prototype class for testing solver implementations */
interface TestAnd : SolverTest {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestAnd =
            TestAndImpl(solverFactory)
    }

    /** Test presence of correct built-ins */
    fun testTermIsFreeVariable()

    fun testWithSubstitution()

    fun testFailIsCallable()

    fun testNoFooIsCallable()

    fun testTrueVarCallable()

}