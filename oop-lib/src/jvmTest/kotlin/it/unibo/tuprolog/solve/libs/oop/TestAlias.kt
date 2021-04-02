package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest

interface TestAlias : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestAlias =
            TestAliasImpl(solverFactory)
    }

    fun testDefaultAliases()

    fun testAliasIsBacktrackable()

    fun testRegisterAndAlias()
}
