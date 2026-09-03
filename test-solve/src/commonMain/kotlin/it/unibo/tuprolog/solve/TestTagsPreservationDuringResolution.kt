package it.unibo.tuprolog.solve

interface TestTagsPreservationDuringResolution : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTagsPreservationDuringResolution =
            TestTagsPreservationDuringResolutionImpl(solverFactory)
    }

    fun testLeft()

    fun testRight()

    fun testSymmetric()

    fun testNone()
}
