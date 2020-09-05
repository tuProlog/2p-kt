package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestAbolish
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAbolish : TestAbolish, SolverFactory by StreamsSolverFactory  {
    private val prototype = TestAbolish.prototype(this)

    @Test
    @Ignore
    override fun testDoubleAbolish() {
        prototype.testDoubleAbolish()
    }

    @Test
    @Ignore
    override fun testAbolishFoo() {
        prototype.testAbolishFoo()
    }

    @Test
    @Ignore
    override fun testAbolishFooNeg() {
        prototype.testAbolishFooNeg()
    }

    @Test
    @Ignore
    override fun testAbolishFlag() {
        prototype.testAbolishFlag()
    }

    @Test
    @Ignore
    override fun testAbolish() {
        prototype.testAbolish()
    }
}