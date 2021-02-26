package it.unibo.tuprolog.solve

interface TestDirectives : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestDirectives =
            TestDirectivesImpl(solverFactory)
    }

    fun testDynamic1()

    fun testStatic1()

    fun testInitialization1()

    fun testSolve1()

    fun testWrongDirectives()

    fun testSetPrologFlag2()

    fun testOp3()

    fun testFailingInitialization1()

    fun testFailingSolve1()

    fun testExceptionalInitialization1()

    fun testExceptionalSolve1()
}
