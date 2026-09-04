package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTagsPreservationDuringResolution
import kotlin.test.Test

class TestClassicTagsPreservationDuringResolution :
    TestTagsPreservationDuringResolution,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestTagsPreservationDuringResolution.prototype(this)

    @Test
    override fun testUntaggedQueryAgainstTaggedTheory() {
        prototype.testUntaggedQueryAgainstTaggedTheory()
    }

    @Test
    override fun testQueryTaggedLikeTheory() {
        prototype.testQueryTaggedLikeTheory()
    }

    @Test
    override fun testQueryTaggedDifferentlyFromTheory() {
        prototype.testQueryTaggedDifferentlyFromTheory()
    }

    @Test
    override fun testTagOriginDuringComputation() {
        prototype.testTagOriginDuringComputation()
    }
}
