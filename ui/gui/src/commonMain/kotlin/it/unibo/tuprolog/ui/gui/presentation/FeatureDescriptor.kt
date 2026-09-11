package it.unibo.tuprolog.ui.gui.presentation

import it.unibo.tuprolog.ui.gui.identity.FeatureId

data class FeatureDescriptor(
    val id: FeatureId,
    val displayName: String,
    val placement: FeaturePlacement,
    val requiredCapabilities: Set<String> = emptySet(),
)
