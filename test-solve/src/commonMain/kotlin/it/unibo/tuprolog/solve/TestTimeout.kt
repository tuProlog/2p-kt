package it.unibo.tuprolog.solve

interface TestTimeout : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTimeout = TestTimeoutImpl(solverFactory)
    }

    fun testSleep()

    fun testInfiniteFindAll()
}
