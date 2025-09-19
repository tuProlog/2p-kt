package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestUnify
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsUnify :
    TestUnify,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestUnify.prototype(this)

    @Test
    override fun testNumberUnify() {
        prototype.testNumberUnify()
    }

    @Test
    override fun testNumberXUnify() {
        prototype.testNumberXUnify()
    }

    @Test
    @Ignore
    override fun testXYUnify() {
        prototype.testXYUnify()
    }

    @Test
    @Ignore
    override fun testDoubleUnify() {
        prototype.testDoubleUnify()
    }

    @Test
    override fun testFDefUnify() {
        prototype.testFDefUnify()
    }

    @Test
    override fun testDiffNumberUnify() {
        prototype.testDiffNumberUnify()
    }

    @Test
    override fun testDecNumberUnify() {
        prototype.testDecNumberUnify()
    }

    @Test
    override fun testGUnifyFX() {
        prototype.testGUnifyFX()
    }

    @Test
    override fun testFUnify() {
        prototype.testFUnify()
    }

    @Test
    override fun testFMultipleTermUnify() {
        prototype.testFMultipleTermUnify()
    }

    @Test
    override fun testMultipleTermUnify() {
        prototype.testMultipleTermUnify()
    }
}
