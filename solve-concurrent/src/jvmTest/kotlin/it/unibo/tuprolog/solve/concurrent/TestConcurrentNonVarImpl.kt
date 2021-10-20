package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentNonVarImpl :
    TestConcurrentNonVar<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNonVarNumber() = multiRunConcurrentTest { super.testNonVarNumber() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNonVarFoo() = multiRunConcurrentTest { super.testNonVarFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNonVarFooCl() = multiRunConcurrentTest { super.testNonVarFooCl() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNonVarFooAssignment() = multiRunConcurrentTest { super.testNonVarFooAssignment() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNonVarAnyTerm() = multiRunConcurrentTest { super.testNonVarAnyTerm() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNonVar() = multiRunConcurrentTest { super.testNonVar() }
}
