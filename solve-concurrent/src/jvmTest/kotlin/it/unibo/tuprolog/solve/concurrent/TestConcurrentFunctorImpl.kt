package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentFunctorImpl :
    TestConcurrentFunctor<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunArity() = multiRunConcurrentTest { super.testFunArity() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunArityWithSub() = multiRunConcurrentTest { super.testFunArityWithSub() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunArityZero() = multiRunConcurrentTest { super.testFunArityZero() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunMats() = multiRunConcurrentTest { super.testFunMats() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunWrongArity() = multiRunConcurrentTest { super.testFunWrongArity() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunWrongName() = multiRunConcurrentTest { super.testFunWrongName() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunXNameYArity() = multiRunConcurrentTest { super.testFunXNameYArity() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunDecNum() = multiRunConcurrentTest { super.testFunDecNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunConsOf() = multiRunConcurrentTest { super.testFunConsOf() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunEmptyList() = multiRunConcurrentTest { super.testFunEmptyList() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunXYWrongArity() = multiRunConcurrentTest { super.testFunXYWrongArity() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunXNArity() = multiRunConcurrentTest { super.testFunXNArity() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunXAArity() = multiRunConcurrentTest { super.testFunXAArity() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunNumName() = multiRunConcurrentTest { super.testFunNumName() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunFooName() = multiRunConcurrentTest { super.testFunFooName() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunFlag() = multiRunConcurrentTest { super.testFunFlag() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testFunNegativeArity() = multiRunConcurrentTest { super.testFunNegativeArity() }
}
