package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
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
            // this may either fail or not on JS, depending on how the tests are launched
            // while it should always fail on JVM
            // ...
            Solver.classic
            if (Info.PLATFORM.isJava) {
                fail("Solver.classic should not be available at runtime here")
            }
        } catch (e: IllegalStateException) {
            // ... in any case, if it fails, an IllegalStateException should be thrown
            assertEquals(
                "No viable implementation for SolverFactory",
                e.message
            )
        }
    }
}
