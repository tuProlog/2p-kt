package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeMethod

class TestMethodInvocationImpl(
    solverFactory: SolverFactory,
) : TestInvocationImpl(solverFactory),
    TestMethodInvocation {
    override fun caseToResult(case: TestDatum): Term = Atom.of(case.type.name)

    override val invoke: String get() = InvokeMethod.functor
}
