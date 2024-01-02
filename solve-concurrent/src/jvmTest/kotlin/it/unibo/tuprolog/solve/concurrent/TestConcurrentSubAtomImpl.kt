package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Ignore
import kotlin.test.Test

class TestConcurrentSubAtomImpl :
    TestConcurrentSubAtom<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomSubIsVar() = multiRunConcurrentTest { super.testSubAtomSubIsVar() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomSubIsVar2() = multiRunConcurrentTest { super.testSubAtomSubIsVar2() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomSubIsVar3() = multiRunConcurrentTest { super.testSubAtomSubIsVar3() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomDoubleVar4() = multiRunConcurrentTest { super.testSubAtomDoubleVar4() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomInstantiationError() = multiRunConcurrentTest { super.testSubAtomInstantiationError() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomTypeErrorAtomIsInteger() = multiRunConcurrentTest { super.testSubAtomTypeErrorAtomIsInteger() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomTypeErrorSubIsInteger() = multiRunConcurrentTest { super.testSubAtomTypeErrorSubIsInteger() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomTypeErrorBeforeIsNotInteger() = multiRunConcurrentTest { super.testSubAtomTypeErrorBeforeIsNotInteger() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Ignore // does not terminate
    override fun testSubAtomTypeErrorLengthIsNotInteger() =
        multiRunConcurrentTest { super.testSubAtomTypeErrorLengthIsNotInteger() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testSubAtomTypeErrorAfterIsNotInteger() = multiRunConcurrentTest { super.testSubAtomTypeErrorAfterIsNotInteger() }
}
