package it.unibo.tuprolog.ui.swing.feature

import it.unibo.tuprolog.ui.gui.identity.FeatureId

/** An immutable, by-[FeatureId] lookup over [renderers]; construction fails fast if two share a [FeatureId]. */
class SwingFeatureRendererRegistry(
    renderers: Iterable<SwingFeatureRenderer> = emptyList(),
) {
    private val materialised = renderers.toList()
    private val values =
        materialised.associateBy { it.featureId }.also { indexed ->
            require(indexed.size == materialised.size) { "Duplicate Swing feature renderer" }
        }

    /** The renderer registered for [featureId], or `null` if none is. */
    fun renderer(featureId: FeatureId): SwingFeatureRenderer? = values[featureId]

    /** Every registered renderer, in registration order. */
    fun all(): Collection<SwingFeatureRenderer> = values.values
}
