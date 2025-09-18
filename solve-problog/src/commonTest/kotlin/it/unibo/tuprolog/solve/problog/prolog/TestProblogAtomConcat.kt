package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomConcat
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAtomConcat :
    TestAtomConcat,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestAtomConcat.prototype(this)

    @Test
    override fun testAtomConcatThirdIsVar() {
        prototype.testAtomConcatThirdIsVar()
    }

    @Test
    override fun testAtomConcatFails() {
        prototype.testAtomConcatFails()
    }

    @Test
    override fun testAtomConcatSecondIsVar() {
        prototype.testAtomConcatSecondIsVar()
    }

    @Test
    override fun testAtomConcatFirstIsVar() {
        prototype.testAtomConcatFirstIsVar()
    }
}
