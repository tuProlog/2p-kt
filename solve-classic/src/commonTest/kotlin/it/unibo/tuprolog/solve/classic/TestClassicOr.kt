package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestOr
import kotlin.test.Test

class TestClassicOr :
    TestOr,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestOr.prototype(this)

    @Test
    override fun testTrueOrFalse() {
        prototype.testTrueOrFalse()
    }

    @Test
    override fun testCutFalseOrTrue() {
        prototype.testCutFalseOrTrue()
    }

    @Test
    override fun testCutCall() {
        prototype.testCutCall()
    }

    @Test
    override fun testCutAssignedValue() {
        prototype.testCutAssignedValue()
    }

    @Test
    override fun testOrDoubleAssignment() {
        prototype.testOrDoubleAssignment()
    }
}
