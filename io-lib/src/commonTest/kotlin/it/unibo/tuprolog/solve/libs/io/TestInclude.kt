package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest

interface TestInclude : SolverTest {

    companion object {
        fun prototype(solverFactory: SolverFactory): TestInclude =
            TestIncludeImpl(solverFactory)
    }

    fun testLocalInclude()

    fun testLocalLoad()

    fun testRemoteInclude()

    fun testRemoteLoad()

    fun testMissingInclude()

    fun testMissingLoad()
}
