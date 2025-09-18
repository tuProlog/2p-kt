package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.SolverTest

interface TestStrictInvocation :
    TestInvocation,
    SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestStrictInvocation = TestStrictInvocationImpl(solverFactory)
    }
}
