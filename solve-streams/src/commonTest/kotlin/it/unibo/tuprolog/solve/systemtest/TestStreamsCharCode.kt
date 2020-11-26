package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestCharCode
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsCharCode : TestCharCode, SolverFactory by StreamsSolverFactory {
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
    @Ignore
    override fun testCharCodeTypeError() {
        prototype.testCharCodeTypeError()
    }

    @Test
    override fun testCharCodeFails() {
        prototype.testCharCodeFails()
    }
}
