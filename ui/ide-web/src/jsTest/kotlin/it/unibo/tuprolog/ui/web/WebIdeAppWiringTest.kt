package it.unibo.tuprolog.ui.web

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parseAsTerm
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.theory.parsing.parseAsTheory
import kotlin.test.Test
import kotlin.test.assertIs

/**
 * Exercises the exact solver [main] wires up, in a real browser via jsBrowserTest.
 *
 * Solver.prolog resolves PrologSolverFactory through a runtime `require("2p-solve-prolog")` by string
 * module name (see solve/src/jsMain/.../SolverExtensionsJs.kt), which only works when Kotlin/JS modules are
 * resolved by Node at runtime; webpack bundles ide-web ahead of time instead, so that lookup used to fail as
 * soon as `main` referenced it. WebIdeApp.kt now imports PrologSolverFactory directly instead — this test
 * proves that import actually resolves and produces a working solver once webpack has bundled it, which
 * compiling alone does not.
 */
class WebIdeAppWiringTest {
    @Test
    fun `PrologSolverFactory builds a working solver once bundled by webpack`() {
        val solver =
            PrologSolverFactory
                .newBuilder()
                .staticKb("p(1).\np(2).".parseAsTheory())
                .buildMutable()
        val solution = solver.solveOnce("p(X).".parseAsTerm() as Struct)
        assertIs<Solution.Yes>(solution)
    }
}
