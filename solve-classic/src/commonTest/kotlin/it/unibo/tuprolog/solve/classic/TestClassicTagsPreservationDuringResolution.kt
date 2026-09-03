package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTagsPreservationDuringResolution
import kotlin.test.Test

class TestClassicTagsPreservationDuringResolution :
    TestTagsPreservationDuringResolution,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestTagsPreservationDuringResolution.prototype(this)

    @Test
    override fun testLeft() {
        prototype.testLeft()
    }

    @Test
    override fun testRight() {
        prototype.testRight()
    }

    @Test
    override fun testSymmetric() {
        prototype.testSymmetric()
    }

    @Test
    override fun testNone() {
        prototype.testNone()
    }
}
