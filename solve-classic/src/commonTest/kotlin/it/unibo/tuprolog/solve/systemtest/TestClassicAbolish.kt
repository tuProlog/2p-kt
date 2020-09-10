package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAbolish
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicAbolish : TestAbolish, SolverFactory by ClassicSolverFactory {
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