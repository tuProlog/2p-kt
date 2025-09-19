package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAbolish
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAbolish :
    TestAbolish,
    SolverFactory by StreamsSolverFactory {
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
