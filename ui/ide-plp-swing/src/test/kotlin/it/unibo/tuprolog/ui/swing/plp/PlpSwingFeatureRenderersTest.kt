package it.unibo.tuprolog.ui.swing.plp

import it.unibo.tuprolog.ui.gui.controller.GuiAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.controller.GuiEffect
import it.unibo.tuprolog.ui.gui.controller.GuiEvent
import it.unibo.tuprolog.ui.gui.identity.DocumentId
import it.unibo.tuprolog.ui.gui.identity.PageId
import it.unibo.tuprolog.ui.gui.identity.SolverProfileId
import it.unibo.tuprolog.ui.gui.model.ApplicationState
import it.unibo.tuprolog.ui.gui.model.FeatureValue
import it.unibo.tuprolog.ui.gui.model.GuiState
import it.unibo.tuprolog.ui.gui.model.PageContent
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import it.unibo.tuprolog.ui.gui.model.WorkspaceConfiguration
import it.unibo.tuprolog.ui.gui.model.WorkspaceState
import it.unibo.tuprolog.ui.gui.plp.PlpFeatureKeys
import it.unibo.tuprolog.ui.swing.SwingFeatureContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.swing.JLabel
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private object InertGuiController : GuiController {
    override val state: StateFlow<GuiState> =
        MutableStateFlow(
            GuiState(
                ApplicationState(),
                WorkspaceState(
                    configuration = WorkspaceConfiguration(defaultSolverProfileId = SolverProfileId("test")),
                ),
            ),
        )
    override val events: SharedFlow<GuiEvent> = MutableSharedFlow()
    override val effects: Flow<GuiEffect> = MutableSharedFlow()

    override suspend fun dispatch(action: GuiAction) = Unit

    override suspend fun shutdown() = Unit
}

class PlpSwingFeatureRenderersTest {
    private val page =
        PageState(id = PageId("page"), title = "page", content = PageContent.DocumentReference(DocumentId("doc")))

    // Dispatchers.Unconfined keeps this fixture's dispatches synchronous for assertions; there is nothing
    // downstream to inject a dispatcher into here, unlike the production code this test exercises.
    @Suppress("InjectDispatcher")
    private val context = SwingFeatureContext(InertGuiController, CoroutineScope(Dispatchers.Unconfined))

    @Test
    fun `probability renderer formats percentages and falls back when absent`() {
        SwingUtilities.invokeAndWait {
            val renderer = ProbabilitySwingFeatureRenderer()
            val label = renderer.createComponent(context) as JLabel

            renderer.render(label, page, PageFeatureState())
            assertEquals("No probabilistic solution", label.text)

            renderer.render(
                label,
                page,
                PageFeatureState(mapOf(PlpFeatureKeys.PROBABILITY to FeatureValue.Number(0.25))),
            )
            assertEquals("Probability: 25%", label.text)

            renderer.render(
                label,
                page,
                PageFeatureState(mapOf(PlpFeatureKeys.PROBABILITY to FeatureValue.Number(1.0 / 3.0))),
            )
            assertEquals("Probability: 33.333333%", label.text)
        }
    }

    @Test
    fun `bdd renderer only shows a graph once a diagram becomes available`() {
        SwingUtilities.invokeAndWait {
            val renderer = BddSwingFeatureRenderer()
            val panel = renderer.createComponent(context)

            renderer.render(panel, page, PageFeatureState())
            assertTrue(collectLabels(panel).any { it.contains("No binary decision diagram") })

            renderer.render(
                panel,
                page,
                PageFeatureState(
                    mapOf(
                        PlpFeatureKeys.BDD_AVAILABLE to FeatureValue.BooleanValue(true),
                        PlpFeatureKeys.BDD_DOT to FeatureValue.Text("digraph { a -> b }"),
                    ),
                ),
            )
            assertTrue(collectLabels(panel).any { it == "Binary decision diagram" })
        }
    }

    private fun collectLabels(component: java.awt.Component): List<String> =
        when (component) {
            is JLabel -> listOf(component.text)
            is java.awt.Container -> component.components.flatMap(::collectLabels)
            else -> emptyList()
        }
}
