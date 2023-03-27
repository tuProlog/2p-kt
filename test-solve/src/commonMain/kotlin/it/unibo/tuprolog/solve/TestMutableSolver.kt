package it.unibo.tuprolog.solve

interface TestMutableSolver : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestMutableSolver =
            TestMutableSolverImpl(solverFactory)
    }

    fun testSetStdIn()

    fun testSetStdOut()

    fun testSetStdErr()

    fun testSetStdWarn()
}
