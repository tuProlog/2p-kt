package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
import it.unibo.tuprolog.solve.classic.ClassicSolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class TestStaticFactory {
    @Test
    fun testStaticSolverFactoryForClassic() {
        assertNotNull(Solver.classic)
        val solver = Solver.classic.solverOf()
        assertNotNull(solver)
        assertTrue { solver is ClassicSolver }
    }

    @Test
    fun testStaticSolverFactoryForStreams() {
        try {
            // this may either fail or not on JS, depending on how the tests are launched
            // while it should always fail on JVM
            // ...
            Solver.streams
            if (Info.PLATFORM.isJava) {
                fail("Solver.streams should not be available at runtime here")
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
