package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFloat
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogFloat :
    TestFloat,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestFloat.prototype(this)

    @Test
    override fun testFloatDec() {
        prototype.testFloatDec()
    }

    @Test
    override fun testFloatDecNeg() {
        prototype.testFloatDecNeg()
    }

    @Test
    override fun testFloatNat() {
        prototype.testFloatNat()
    }

    @Test
    override fun testFloatAtom() {
        prototype.testFloatAtom()
    }

    @Test
    override fun testFloatX() {
        prototype.testFloatX()
    }
}
