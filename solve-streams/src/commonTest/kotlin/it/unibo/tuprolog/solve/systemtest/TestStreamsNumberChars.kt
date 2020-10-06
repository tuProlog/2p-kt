package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestNumberChars
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsNumberChars : TestNumberChars, SolverFactory by StreamsSolverFactory {
    private val prototype = TestNumberChars.prototype(this)

    @Test
    override fun testNumberCharsListIsVar() {
        prototype.testNumberCharsListIsVar()
    }

    @Test
    override fun testNumberCharsOK() {
        prototype.testNumberCharsOK()
    }

    @Test
    override fun testNumberCharsNumIsVar() {
        prototype.testNumberCharsNumIsVar()
    }

    @Test
    override fun testNumberCharsNumNegativeIsVar() {
        prototype.testNumberCharsNumNegativeIsVar()
    }

    @Test
    override fun testNumberCharsSpace() {
        prototype.testNumberCharsSpace()
    }

    override fun testNumberCharsDecimalNumber() {
        prototype.testNumberCharsDecimalNumber()
    }

    @Test
    @Ignore
    override fun testNumberCharsCompleteCase() {
        prototype.testNumberCharsCompleteCase()
    }

    @Test
    @Ignore
    override fun testNumberCharsInstationErrror() {
        prototype.testNumberCharsInstationErrror()
    }
}
