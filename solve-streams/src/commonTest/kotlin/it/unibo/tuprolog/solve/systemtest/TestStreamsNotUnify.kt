package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestNotUnify
import kotlin.test.Test

class TestStreamsNotUnify : TestNotUnify, SolverFactory by StreamsSolverFactory {

    private val prototype = TestNotUnify.prototype(this)

    @Test
    override fun testNumberNotUnify() {
        prototype.testNumberNotUnify()
    }

    @Test
    override fun testNumberXNotUnify() {
        prototype.testNumberXNotUnify()
    }

    @Test
    override fun testXYNotUnify() {
        prototype.testXYNotUnify()
    }

    @Test
    override fun testDoubleNotUnify() {
        prototype.testDoubleNotUnify()
    }

    @Test
    override fun testFDefNotUnify() {
        prototype.testFDefNotUnify()
    }

    @Test
    override fun testDiffNumberNotUnify() {
        prototype.testDiffNumberNotUnify()
    }

    @Test
    override fun testDecNumberNotUnify() {
        prototype.testDecNumberNotUnify()
    }

    @Test
    override fun testGNotUnifyFX() {
        prototype.testGNotUnifyFX()
    }

    @Test
    override fun testFNotUnify() {
        prototype.testFNotUnify()
    }

    @Test
    override fun testFMultipleTermNotUnify() {
        prototype.testFMultipleTermNotUnify()
    }
}
