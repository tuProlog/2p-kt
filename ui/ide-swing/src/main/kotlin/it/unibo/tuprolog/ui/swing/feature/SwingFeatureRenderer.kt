package it.unibo.tuprolog.ui.swing.feature

import it.unibo.tuprolog.ui.gui.identity.FeatureId
import it.unibo.tuprolog.ui.gui.model.PageFeatureState
import it.unibo.tuprolog.ui.gui.model.PageState
import javax.swing.JComponent

/** Toolkit-specific renderer for a semantic extension feature. */
interface SwingFeatureRenderer {
    /** Identifies this feature within [PageFeatureState.values], and doubles as [displayName]'s default. */
    val featureId: FeatureId

    /** Human-readable tab title. */
    val displayName: String get() = featureId.value

    /** Solver capabilities required for the renderer to be useful. */
    val requiredCapabilities: Set<String> get() = emptySet()

    /** Builds this feature's (initially empty) tab component once, up front - populated later by [render]. */
    fun createComponent(context: SwingFeatureContext): JComponent

    /** Updates [component] (as returned by [createComponent]) to reflect [page]'s current [state]. */
    fun render(
        component: JComponent,
        page: PageState,
        state: PageFeatureState,
    )
}
