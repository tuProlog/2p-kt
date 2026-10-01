package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFail
import kotlin.test.Test

class TestPrologFail :
    TestFail,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestFail.prototype(this)

    @Test
    override fun testFail() {
        prototype.testFail()
    }

    @Test
    override fun testUndefPred() {
        prototype.testUndefPred()
    }

    @Test
    override fun testSetFlagFail() {
        prototype.testSetFlagFail()
    }

    @Test
    override fun testSetFlagWarning() {
        prototype.testSetFlagWarning()
    }
}
