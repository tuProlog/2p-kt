package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.libs.oop.primitives.InvokeStrict

class TestStrictInvocationImpl(
    solverFactory: SolverFactory,
) : TestInvocationImpl(solverFactory),
    TestStrictInvocation {
    override fun caseToResult(case: TestDatum): Term = ObjectRef.of(case.type.name)

    override val invoke: String get() = InvokeStrict.functor
}
