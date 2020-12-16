package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest

interface TestConsult : SolverTest {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestConsult =
            TestConsultImpl(solverFactory)
    }

    fun testApi()

    fun testConsultWorksLocally()

    fun testConsultWorksRemotely()
}
