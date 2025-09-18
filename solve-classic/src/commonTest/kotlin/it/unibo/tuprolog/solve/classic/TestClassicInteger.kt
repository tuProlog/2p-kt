package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestInteger
import kotlin.test.Test

class TestClassicInteger :
    TestInteger,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestInteger.prototype(this)

    @Test
    override fun testIntPositiveNum() {
        prototype.testIntPositiveNum()
    }

    @Test
    override fun testIntNegativeNum() {
        prototype.testIntNegativeNum()
    }

    @Test
    override fun testIntDecNum() {
        prototype.testIntDecNum()
    }

    @Test
    override fun testIntX() {
        prototype.testIntX()
    }

    @Test
    override fun testIntAtom() {
        prototype.testIntAtom()
    }
}
