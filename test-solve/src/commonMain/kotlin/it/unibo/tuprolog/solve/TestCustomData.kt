package it.unibo.tuprolog.solve

interface TestCustomData : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestCustomData = TestCustomDataImpl(solverFactory)
    }

    fun testApi()

    fun testEphemeralData()

    fun testDurableData()

    fun testPersistentData()
}
