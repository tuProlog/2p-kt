package it.unibo.tuprolog.ui.gui.plp

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.setBinaryDecisionDiagram
import it.unibo.tuprolog.solve.setProbability
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class SolutionConversionsTest {
    @Test
    fun `a probabilistic yes solution populates both probability and BDD feature state`() {
        val solution =
            Solution
                .yes(Struct.of("rain"))
                .setProbability(0.25)
                .setBinaryDecisionDiagram(BinaryDecisionDiagram.variableOf(Atom.of("rain")))

        val features = solution.plpFeatureState()

        assertEquals(
            0.25,
            assertIs<FeatureValue.Number>(
                features.getValue(PlpGuiIds.SOLUTION_DETAILS).getValue(PlpFeatureKeys.PROBABILITY),
            ).value,
        )
        assertIs<FeatureValue.Text>(
            features.getValue(PlpGuiIds.BDD_INSPECTOR).getValue(PlpFeatureKeys.BDD_DOT),
        )
    }

    @Test
    fun `a plain yes solution without tags defaults to certain and reports no BDD`() {
        val features = Solution.yes(Struct.of("rain")).plpFeatureState()

        assertEquals(
            1.0,
            assertIs<FeatureValue.Number>(
                features.getValue(PlpGuiIds.SOLUTION_DETAILS).getValue(PlpFeatureKeys.PROBABILITY),
            ).value,
        )
        assertEquals(
            false,
            assertIs<FeatureValue.BooleanValue>(
                features.getValue(PlpGuiIds.BDD_INSPECTOR).getValue(PlpFeatureKeys.BDD_AVAILABLE),
            ).value,
        )
    }

    @Test
    fun `a no solution reports no probability`() {
        val features = Solution.no(Struct.of("rain")).plpFeatureState()

        assertNull(features.getValue(PlpGuiIds.SOLUTION_DETAILS)[PlpFeatureKeys.PROBABILITY])
    }
}
