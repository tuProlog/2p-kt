package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.plp.PlpFeatureKeys
import it.unibo.tuprolog.ui.gui.plp.PlpGuiIds
import it.unibo.tuprolog.ui.gui.plp.formatProbabilityPercentage
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.swing.SwingFeatureContext
import it.unibo.tuprolog.ui.swing.SwingFeatureRenderer
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingConstants

private const val LABEL_BORDER_INSET = 16
private const val LABEL_FONT_SIZE = 18f

class ProbabilitySwingFeatureRenderer : SwingFeatureRenderer {
    override val featureId = PlpGuiIds.SOLUTION_DETAILS
    override val displayName: String = "Probability"
    override val requiredCapabilities: Set<String> = setOf(SolverCapabilities.PROBABILISTIC_SOLUTIONS)

    override fun createComponent(context: SwingFeatureContext): JComponent =
        JLabel("No probabilistic solution", SwingConstants.CENTER).apply {
            name = "probabilityLabel"
            border =
                BorderFactory.createEmptyBorder(
                    LABEL_BORDER_INSET,
                    LABEL_BORDER_INSET,
                    LABEL_BORDER_INSET,
                    LABEL_BORDER_INSET,
                )
            font = font.deriveFont(Font.BOLD, LABEL_FONT_SIZE)
        }

    override fun render(
        component: JComponent,
        page: PageState,
        state: PageFeatureState,
    ) {
        val label = component as JLabel
        val probability = (state.values[PlpFeatureKeys.PROBABILITY] as? FeatureValue.Number)?.value
        label.text =
            probability?.let { "Probability: ${formatProbabilityPercentage(it)}" } ?: "No probabilistic solution"
    }
}
