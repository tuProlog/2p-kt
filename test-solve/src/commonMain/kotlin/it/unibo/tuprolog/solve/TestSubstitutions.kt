package it.unibo.tuprolog.solve

interface TestSubstitutions : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestSubstitutions =
            TestSubstitutionsImpl(solverFactory)
    }

    fun interestingVariablesAreNotObliterated()

    fun uninterestingVariablesAreObliterated()
}
