package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.controller.GuiAction
import it.unibo.tuprolog.ui.gui.controller.GuiController
import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import kotlinx.coroutines.CoroutineScope
import javax.swing.JComponent

/** Services exposed to a toolkit-specific feature renderer without leaking them into the common extension API. */
data class SwingFeatureContext(
    val controller: GuiController,
    val scope: CoroutineScope,
) {
    fun dispatch(action: GuiAction) {
        scope.dispatch { controller.dispatch(action) }
    }
}

/** Toolkit-specific renderer for a semantic extension feature. */
interface SwingFeatureRenderer {
    val featureId: FeatureId

    /** Human-readable tab title. */
    val displayName: String get() = featureId.value

    /** Solver capabilities required for the renderer to be useful. */
    val requiredCapabilities: Set<String> get() = emptySet()

    fun createComponent(context: SwingFeatureContext): JComponent

    fun render(
        component: JComponent,
        page: PageState,
        state: PageFeatureState,
    )
}

class SwingFeatureRendererRegistry(
    renderers: Iterable<SwingFeatureRenderer> = emptyList(),
) {
    private val materialised = renderers.toList()
    private val values =
        materialised.associateBy { it.featureId }.also { indexed ->
            require(indexed.size == materialised.size) { "Duplicate Swing feature renderer" }
        }

    fun renderer(featureId: FeatureId): SwingFeatureRenderer? = values[featureId]

    fun all(): Collection<SwingFeatureRenderer> = values.values
}
