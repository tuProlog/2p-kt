package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAtomImpl :
    TestConcurrentAtom<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomAtom() = multiRunConcurrentTest { super.testAtomAtom() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomString() = multiRunConcurrentTest { super.testAtomString() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomAofB() = multiRunConcurrentTest { super.testAtomAofB() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomVar() = multiRunConcurrentTest { super.testAtomVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomEmptyList() = multiRunConcurrentTest { super.testAtomEmptyList() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomNum() = multiRunConcurrentTest { super.testAtomNum() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testAtomNumDec() = multiRunConcurrentTest { super.testAtomNumDec() }
}
