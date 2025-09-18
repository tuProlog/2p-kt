package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSubAtom
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamSubAtom :
    TestSubAtom,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestSubAtom.prototype(this)

    @Test
    override fun testSubAtomSubIsVar() {
        prototype.testSubAtomSubIsVar()
    }

    @Test
    override fun testSubAtomSubIsVar2() {
        prototype.testSubAtomSubIsVar2()
    }

    @Test
    override fun testSubAtomSubIsVar3() {
        prototype.testSubAtomSubIsVar3()
    }

    @Test
    override fun testSubAtomDoubleVar4() {
        prototype.testSubAtomDoubleVar4()
    }

    @Test
    @Ignore
    override fun testSubAtomInstantiationError() {
        prototype.testSubAtomInstantiationError()
    }

    @Test
    @Ignore
    override fun testSubAtomTypeErrorAtomIsInteger() {
        prototype.testSubAtomTypeErrorAtomIsInteger()
    }

    @Test
    @Ignore
    override fun testSubAtomTypeErrorSubIsInteger() {
        prototype.testSubAtomTypeErrorSubIsInteger()
    }

    @Test
    @Ignore
    override fun testSubAtomTypeErrorBeforeIsNotInteger() {
        prototype.testSubAtomTypeErrorBeforeIsNotInteger()
    }

    @Test
    @Ignore
    override fun testSubAtomTypeErrorLengthIsNotInteger() {
        prototype.testSubAtomTypeErrorLengthIsNotInteger()
    }

    @Test
    @Ignore
    override fun testSubAtomTypeErrorAfterIsNotInteger() {
        prototype.testSubAtomTypeErrorAfterIsNotInteger()
    }
}
