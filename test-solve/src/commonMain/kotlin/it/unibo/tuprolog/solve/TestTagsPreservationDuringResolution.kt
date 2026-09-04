package it.unibo.tuprolog.solve

interface TestTagsPreservationDuringResolution : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTagsPreservationDuringResolution =
            TestTagsPreservationDuringResolutionImpl(solverFactory)
    }

    fun testUntaggedQueryAgainstTaggedTheory()

    fun testQueryTaggedLikeTheory()

    fun testQueryTaggedDifferentlyFromTheory()

    fun testTagOriginDuringComputation()
}
