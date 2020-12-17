package it.unibo.tuprolog.solve

import it.unibo.tuprolog.solve.streams.StreamsSolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class TestStaticFactory {
    @Test
    fun testStaticSolverFactoryForStreams() {
        assertNotNull(Solver.streams)
        val solver = Solver.streams.solverOf()
        assertNotNull(solver)
        assertTrue { solver is StreamsSolver }
    }

    @Test
    fun testStaticSolverFactoryForClassic() {
        try {
            Solver.classic
            fail("Solver.classic should not be available at runtime here")
        } catch (e: IllegalStateException) {
            assertEquals(
                "No viable implementation for SolverFactory",
                e.message
            )
        }
    }
}
