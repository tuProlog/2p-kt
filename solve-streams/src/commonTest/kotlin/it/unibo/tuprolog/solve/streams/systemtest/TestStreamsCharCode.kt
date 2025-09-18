package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCharCode
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsCharCode :
    TestCharCode,
    SolverFactory by StreamsSolverFactory {
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
