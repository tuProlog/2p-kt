package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCharCode
import kotlin.test.Test

class TestPrologCharCode :
    TestCharCode,
    SolverFactory by PrologSolverFactory {
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
