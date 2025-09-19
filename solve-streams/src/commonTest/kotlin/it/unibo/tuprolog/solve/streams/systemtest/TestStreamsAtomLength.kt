package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomLength
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAtomLength :
    TestAtomLength,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestAtomLength.prototype(this)

    @Test
    override fun testAtomLengthNoVar() {
        prototype.testAtomLengthNoVar()
    }

    @Test
    override fun testAtomLengthSecondIsVar() {
        prototype.testAtomLengthSecondIsVar()
    }

    @Test
    @Ignore
    override fun testAtomLengthFirstIsVar() {
        prototype.testAtomLengthFirstIsVar()
    }

    @Test
    override fun testAtomLengthSecondIsVar2() {
        prototype.testAtomLengthSecondIsVar2()
    }

    @Test
    override fun testAtomLengthFail() {
        prototype.testAtomLengthFail()
    }
}
