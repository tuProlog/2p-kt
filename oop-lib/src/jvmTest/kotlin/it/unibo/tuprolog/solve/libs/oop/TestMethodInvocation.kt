package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest

interface TestMethodInvocation :
    TestInvocation,
    SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestMethodInvocation = TestMethodInvocationImpl(solverFactory)
    }
}
