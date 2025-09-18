package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestUnify
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogUnify :
    TestUnify,
    SolverFactory by ProblogSolverFactory {
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
    override fun testXYUnify() {
        prototype.testXYUnify()
    }

    @Test
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
