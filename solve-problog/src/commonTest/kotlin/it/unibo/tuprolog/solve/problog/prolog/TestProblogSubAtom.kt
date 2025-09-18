package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSubAtom
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogSubAtom :
    TestSubAtom,
    SolverFactory by ProblogSolverFactory {
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
    override fun testSubAtomInstantiationError() {
        prototype.testSubAtomInstantiationError()
    }

    @Test
    override fun testSubAtomTypeErrorAtomIsInteger() {
        prototype.testSubAtomTypeErrorAtomIsInteger()
    }

    @Test
    override fun testSubAtomTypeErrorSubIsInteger() {
        prototype.testSubAtomTypeErrorSubIsInteger()
    }

    @Test
    override fun testSubAtomTypeErrorBeforeIsNotInteger() {
        prototype.testSubAtomTypeErrorBeforeIsNotInteger()
    }

    @Test
    override fun testSubAtomTypeErrorLengthIsNotInteger() {
        prototype.testSubAtomTypeErrorLengthIsNotInteger()
    }

    @Test
    override fun testSubAtomTypeErrorAfterIsNotInteger() {
        prototype.testSubAtomTypeErrorAfterIsNotInteger()
    }
}
