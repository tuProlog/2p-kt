package it.unibo.tuprolog.ui.swing

import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import javax.swing.JComponent

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
