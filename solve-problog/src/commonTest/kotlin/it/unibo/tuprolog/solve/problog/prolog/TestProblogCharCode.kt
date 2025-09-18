package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCharCode
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogCharCode :
    TestCharCode,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestCharCode.prototype(this)

    @Test
    override fun testCharCodeSecondIsVar() {
        prototype.testCharCodeSecondIsVar()
    }

    @Test
    override fun testCharCodeFirstIsVar() {
        prototype.testCharCodeFirstIsVar()
    }

    @Test
    override fun testCharCodeTypeError() {
        prototype.testCharCodeTypeError()
    }

    @Test
    override fun testCharCodeFails() {
        prototype.testCharCodeFails()
    }
}
