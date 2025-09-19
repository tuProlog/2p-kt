package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomCodes
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsAtomCodes :
    TestAtomCodes,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestAtomCodes.prototype(this)

    @Test
    override fun testAtomCodesSecondIsVar1() {
        prototype.testAtomCodesSecondIsVar1()
    }

    @Test
    override fun testAtomCodesSecondIsVar() {
        prototype.testAtomCodesSecondIsVar()
    }

    @Test
    override fun testAtomCodesFirstIsVar() {
        prototype.testAtomCodesFirstIsVar()
    }

    @Test
    override fun testAtomCodesNoVar() {
        prototype.testAtomCodesNoVar()
    }

    @Test
    override fun testAtomCodesFail() {
        prototype.testAtomCodesFail()
    }
}
