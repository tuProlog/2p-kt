package it.unibo.tuprolog.solve

import it.unibo.tuprolog.Info
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.fail

@Suppress("DEPRECATION", "ConstPropertyName", "ktlint:standard:property-naming")
class TestStaticFactoryImpl(
    private val expectations: Expectations,
) : TestStaticFactory {
    companion object {
        private const val classicSolverClass = "it.unibo.tuprolog.solve.classic.ClassicSolver"
        private const val streamsSolverClass = "it.unibo.tuprolog.solve.streams.StreamsSolver"
        private const val problogSolverClass = "it.unibo.tuprolog.solve.problog.ProblogSolver"
        private const val prologSolverClass = classicSolverClass

        private const val classicName = "classic"
        private const val streamsName = "streams"
        private const val problogName = "problog"
        private const val prologName = "prolog"
    }

    override fun testStaticSolverFactoryForClassic() {
        if (expectations.classicShouldWork) {
            testStaticSolverFactoryShouldWork(classicSolverClass) { Solver.classic }
        } else {
            testStaticFactoryShouldFail(classicName) { Solver.classic }
        }
    }

    override fun testStaticSolverFactoryForStreams() {
        if (expectations.streamsShouldWork) {
            testStaticSolverFactoryShouldWork(streamsSolverClass) { Solver.streams }
        } else {
            testStaticFactoryShouldFail(streamsName) { Solver.streams }
        }
    }

    override fun testStaticSolverFactoryForProlog() {
        if (expectations.prologShouldWork) {
            testStaticSolverFactoryShouldWork(prologSolverClass) { Solver.prolog }
        } else {
            testStaticFactoryShouldFail(prologName) { Solver.prolog }
        }
    }

    override fun testStaticSolverFactoryForProblog() {
        if (expectations.problogShouldWork) {
            testStaticSolverFactoryShouldWork(problogSolverClass) { Solver.problog }
        } else {
            testStaticFactoryShouldFail(problogName) { Solver.problog }
        }
    }

    fun testStaticSolverFactoryShouldWork(
        `class`: String,
        factory: () -> SolverFactory,
    ) {
        assertNotNull(factory())
        val solver = factory().solverOf()
        assertNotNull(solver)
        assertClassNameIs(solver::class, `class`)
        assertNotNull(factory().newBuilder())
        assertNotSame(factory().newBuilder(), factory().newBuilder())
        val solver2 = factory().newBuilder().build()
        assertNotNull(solver2)
        assertClassNameIs(solver2::class, `class`)
    }

    private fun testStaticFactoryShouldFail(
        name: String,
        factory: () -> SolverFactory,
    ) {
        try {
            // this may either fail or not on JS, depending on how the tests are launched
            // while it should always fail on JVM
            // ...
            factory()
            if (Info.PLATFORM.isJava) {
                fail("Solver.$name should not be available at runtime here")
            }
        } catch (e: IllegalStateException) {
            // ... in any case, if it fails, an IllegalStateException should be thrown
            assertEquals(
                true,
                e.message?.startsWith("No viable implementation for SolverFactory"),
            )
        }
    }
}
