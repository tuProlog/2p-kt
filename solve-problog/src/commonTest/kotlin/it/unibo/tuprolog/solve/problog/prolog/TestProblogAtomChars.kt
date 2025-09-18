package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomChars
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAtomChars :
    TestAtomChars,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestAtomChars.prototype(this)

    @Test
    override fun atomCharsTestFirstIsVar() {
        prototype.atomCharsTestFirstIsVar()
    }

    @Test
    override fun atomCharsTestYes() {
        prototype.atomCharsTestYes()
    }

    @Test
    override fun atomCharsTestOneCharIsVar() {
        prototype.atomCharsTestOneCharIsVar()
    }

    @Test
    override fun atomCharsTestFailure() {
        prototype.atomCharsTestFailure()
    }

    @Test
    override fun atomCharsTestEmpty() {
        prototype.atomCharsTestEmpty()
    }

    @Test
    override fun atomCharsTestListHead() {
        prototype.atomCharsTestListHead()
    }

    @Test
    override fun atomCharsTestIstantationErrorCheck() {
        prototype.atomCharsTestIstantationErrorCheck()
    }

    @Test
    override fun atomCharsTestTypeErrorCheck() {
        prototype.atomCharsTestTypeErrorCheck()
    }
}
