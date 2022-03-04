package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentAtomCharsImpl :
    TestConcurrentAtomChars<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestFirstIsVar() = multiRunConcurrentTest { super.atomCharsTestFirstIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestYes() = multiRunConcurrentTest { super.atomCharsTestYes() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestOneCharIsVar() = multiRunConcurrentTest { super.atomCharsTestOneCharIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestFailure() = multiRunConcurrentTest { super.atomCharsTestFailure() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestEmpty() = multiRunConcurrentTest { super.atomCharsTestEmpty() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestListHead() = multiRunConcurrentTest { super.atomCharsTestListHead() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestIstantationErrorCheck() = multiRunConcurrentTest { super.atomCharsTestIstantationErrorCheck() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun atomCharsTestTypeErrorCheck() = multiRunConcurrentTest { super.atomCharsTestTypeErrorCheck() }
}
