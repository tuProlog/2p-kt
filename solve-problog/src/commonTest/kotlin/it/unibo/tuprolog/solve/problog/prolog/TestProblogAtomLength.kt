package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomLength
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAtomLength :
    TestAtomLength,
    SolverFactory by ProblogSolverFactory {
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
