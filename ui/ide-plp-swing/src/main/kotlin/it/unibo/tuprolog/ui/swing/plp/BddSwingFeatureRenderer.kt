package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.gui.controller.PageAction
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.plp.PlpFeatureKeys
import it.unibo.tuprolog.ui.gui.plp.PlpGuiIds
import it.unibo.tuprolog.ui.gui.solver.SolverCapabilities
import it.unibo.tuprolog.ui.swing.SwingFeatureContext
import it.unibo.tuprolog.ui.swing.SwingFeatureRenderer
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class BddSwingFeatureRenderer(
    private val graphRenderer: SwingBddGraphRenderer = DotTextSwingBddGraphRenderer,
) : SwingFeatureRenderer {
    override val featureId = PlpGuiIds.BDD_INSPECTOR
    override val displayName: String = "BDD"
    override val requiredCapabilities: Set<String> =
        setOf(SolverCapabilities.PROBABILISTIC_SOLUTIONS, SolverCapabilities.BDD_PRESENTATION)

    override fun createComponent(context: SwingFeatureContext): JComponent = BddPanel(context, graphRenderer)

    override fun render(
        component: JComponent,
        page: PageState,
        state: PageFeatureState,
    ) {
        val available = (state.values[PlpFeatureKeys.BDD_AVAILABLE] as? FeatureValue.BooleanValue)?.value == true
        val dot = (state.values[PlpFeatureKeys.BDD_DOT] as? FeatureValue.Text)?.value
        val title = (state.values[PlpFeatureKeys.BDD_TITLE] as? FeatureValue.Text)?.value
        (component as BddPanel).render(page, if (available) dot else null, title)
    }

    private class BddPanel(
        private val context: SwingFeatureContext,
        private val graphRenderer: SwingBddGraphRenderer,
    ) : JPanel(BorderLayout()) {
        private val heading = JLabel("No binary decision diagram")
        private val graphHost = JPanel(BorderLayout())
        private val copyButton = JButton("Copy DOT")
        private var page: PageState? = null
        private var dot: String? = null

        init {
            border = BorderFactory.createEmptyBorder(6, 6, 6, 6)
            add(
                JPanel(BorderLayout()).apply {
                    add(heading, BorderLayout.CENTER)
                    add(copyButton, BorderLayout.EAST)
                },
                BorderLayout.NORTH,
            )
            add(graphHost, BorderLayout.CENTER)
            copyButton.isEnabled = false
            copyButton.addActionListener {
                val currentPage = page ?: return@addActionListener
                val currentDot = dot ?: return@addActionListener
                context.dispatch(
                    PageAction.ExtensionCommand(
                        pageId = currentPage.id,
                        extensionId = PlpGuiIds.EXTENSION,
                        commandId = PlpGuiIds.COPY_BDD_DOT,
                        payload = mapOf(PlpFeatureKeys.BDD_DOT to currentDot),
                    ),
                )
            }
        }

        fun render(
            page: PageState,
            dot: String?,
            title: String?,
        ) {
            this.page = page
            if (this.dot == dot && heading.text == (title ?: defaultHeading(dot))) return
            this.dot = dot
            val effectiveTitle = title ?: defaultHeading(dot)
            heading.text = effectiveTitle
            copyButton.isEnabled = dot != null
            graphHost.removeAll()
            if (dot == null) {
                graphHost.add(JLabel("No BDD is available for the current solution", SwingConstants.CENTER))
            } else {
                graphHost.add(graphRenderer.createGraphComponent(dot, effectiveTitle), BorderLayout.CENTER)
            }
            graphHost.revalidate()
            graphHost.repaint()
        }

        private fun defaultHeading(dot: String?): String =
            if (dot == null) "No binary decision diagram" else "Binary decision diagram"
    }
}
