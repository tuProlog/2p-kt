package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRecursion
import kotlin.test.Test

class TestPrologRecursion :
    TestRecursion,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestRecursion.prototype(this)

    @Test
    override fun testRecursion1() {
        prototype.testRecursion1()
    }

    @Test
    override fun testRecursion2() {
        prototype.testRecursion2()
    }

    @Test
    override fun testTailRecursion() {
        prototype.testTailRecursion()
    }

    @Test
    override fun testNonTailRecursion() {
        prototype.testNonTailRecursion()
    }
}
