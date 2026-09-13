package it.unibo.tuprolog.ui.swing.feature

import it.unibo.tuprolog.ui.gui.identity.FeatureId

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
