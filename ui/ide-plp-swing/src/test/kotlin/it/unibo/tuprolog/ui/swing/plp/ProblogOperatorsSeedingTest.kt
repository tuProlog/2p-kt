package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.plp.plpFeatureState
import it.unibo.tuprolog.ui.gui.prolog.solverFactoryProfile
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * The ProbLog solver profile's `::` operator must be known before any solver session/resolution ever runs (see
 * [it.unibo.tuprolog.ui.gui.solver.SolverProfile.defaultOperators]), otherwise a page opened with a legit
 * probabilistic clause is flagged as a syntax error until the user runs a query once.
 */
class ProblogOperatorsSeedingTest {
    @Test
    fun problogProfileExposesItsAnnotationOperatorWithoutBuildingASolver() {
        val profile =
            solverFactoryProfile(
                Solver.problog,
                SolverProfileId("problog"),
                "ProbLog",
                setOf(SolverCapabilities.CANCELLATION, SolverCapabilities.PROBABILISTIC_SOLUTIONS),
                Solution::plpFeatureState,
            )
        assertTrue(profile.defaultOperators.any { it.name == "::" })
    }
}
