package it.unibo.tuprolog.solve

interface TestSolutionPresentation : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSolutionPresentation =
            TestSolutionPresentationImpl(solverFactory)
    }

    fun testSolutionWithDandlingVars()
}
