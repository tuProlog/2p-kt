package it.unibo.tuprolog.ui.gui.presentation

import it.unibo.tuprolog.ui.gui.identity.FeatureId

/** Describes one extension-contributed feature tab/panel: where it goes, what it's called, and what solver
 * capabilities a page needs before it's shown. */
data class FeatureDescriptor(
    val id: FeatureId,
    val displayName: String,
    val placement: FeaturePlacement,
    val requiredCapabilities: Set<String> = emptySet(),
)
