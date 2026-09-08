package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.bdd.BinaryDecisionDiagram
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.setBinaryDecisionDiagram
import it.unibo.tuprolog.solve.setProbability
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.plp.PlpFeatureKeys
import it.unibo.tuprolog.ui.gui.plp.PlpGuiIds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class PlpSwingMainTest {
    @Test
    fun `probabilistic solution populates Swing feature state`() {
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
}
