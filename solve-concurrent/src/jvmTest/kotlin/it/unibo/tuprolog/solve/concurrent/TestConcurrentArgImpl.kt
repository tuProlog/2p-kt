package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.SolverFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.test.Test

class TestConcurrentArgImpl :
    TestConcurrentArg<MultiSet>,
    SolverFactory by ConcurrentSolverFactory,
    FromSequence<MultiSet> by ConcurrentFromSequence {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFoo() = multiRunConcurrentTest { super.testArgFromFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFooX() = multiRunConcurrentTest { super.testArgFromFooX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFoo2() = multiRunConcurrentTest { super.testArgFromFoo2() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFooInF() = multiRunConcurrentTest { super.testArgFromFooInF() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFooY() = multiRunConcurrentTest { super.testArgFromFooY() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFooInSecondTerm() = multiRunConcurrentTest { super.testArgFromFooInSecondTerm() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromFooInFoo() = multiRunConcurrentTest { super.testArgFromFooInFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgNumberFromFoo() = multiRunConcurrentTest { super.testArgNumberFromFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgXFromFoo() = multiRunConcurrentTest { super.testArgXFromFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgNumberFromX() = multiRunConcurrentTest { super.testArgNumberFromX() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromAtom() = multiRunConcurrentTest { super.testArgFromAtom() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgFromNumber() = multiRunConcurrentTest { super.testArgFromNumber() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testNegativeArgFromFoo() = multiRunConcurrentTest { super.testNegativeArgFromFoo() }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    override fun testArgAFromFoo() = multiRunConcurrentTest { super.testArgAFromFoo() }
}
