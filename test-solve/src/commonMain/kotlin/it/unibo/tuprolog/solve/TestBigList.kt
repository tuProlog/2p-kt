package it.unibo.tuprolog.solve

interface TestBigList : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestBigList =
            TestBigListImpl(solverFactory)
    }

    fun testBigListGeneration()
}
