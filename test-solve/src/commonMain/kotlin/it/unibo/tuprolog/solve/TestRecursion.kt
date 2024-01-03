package it.unibo.tuprolog.solve

interface TestRecursion : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestRecursion = TestRecursionImpl(solverFactory)
    }

    fun testRecursion1()

    fun testRecursion2()

    fun testTailRecursion()

    fun testNonTailRecursion()
}
