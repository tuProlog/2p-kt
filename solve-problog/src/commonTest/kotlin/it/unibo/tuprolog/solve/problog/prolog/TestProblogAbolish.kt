package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAbolish
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogAbolish :
    TestAbolish,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestAbolish.prototype(this)

    @Test
    override fun testDoubleAbolish() {
        prototype.testDoubleAbolish()
    }

    @Test
    override fun testAbolishFoo() {
        prototype.testAbolishFoo()
    }

    @Test
    override fun testAbolishFooNeg() {
        prototype.testAbolishFooNeg()
    }

    @Test
    override fun testAbolishFlag() {
        prototype.testAbolishFlag()
    }

    @Test
    override fun testAbolish() {
        prototype.testAbolish()
    }
}
