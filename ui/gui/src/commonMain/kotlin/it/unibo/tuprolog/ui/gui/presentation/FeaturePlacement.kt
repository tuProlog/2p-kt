package it.unibo.tuprolog.ui.gui.presentation

/** Where a [FeatureDescriptor] should appear ([region]) and its relative [priority] among others placed there. */
data class FeaturePlacement(
    val region: SemanticRegion,
    val priority: Int = 0,
)
